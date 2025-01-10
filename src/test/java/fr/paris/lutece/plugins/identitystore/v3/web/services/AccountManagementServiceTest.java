/*
 * Copyright (c) 2002-2024, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.identitystore.v3.web.services;

import fr.paris.lutece.plugins.accountmanagement.web.service.AccountManagementService;
import fr.paris.lutece.plugins.accountmanagement.web.service.FederationLinkManagementService;
import fr.paris.lutece.plugins.accountmanagement.web.service.PasswordHistoryService;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.account.AccountDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.account.ChangeAccountResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.account.ChangeFederationLinkResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.account.FederationLinkDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.account.GetAccountResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.account.RequestClient;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.account.SearchListAccountResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.account.SearchListAccountResult;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.account.SearchListFederationLinkResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.account.SearchListPasswordHistortyResponse;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityAccountException;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * test of NotificationService
 */

public class AccountManagementServiceTest extends AbstractServiceTest
{
    @Resource( name = "identityService.accountmanagement.httpAccess" )
    private AccountManagementService accountManagementService;

    @Resource( name = "identityService.federationlinkmanagement.httpAccess" )
    private FederationLinkManagementService federationLinkManagementService;

    @Resource( name = "identityService.passwordhistory.httpAccess" )
    private PasswordHistoryService passwordHistoryService;

    private RequestClient client;
    private final String mail = UUID.randomUUID() + "@mail.com";
    private final String initialPassword = UUID.randomUUID().toString();
    private final String newPassword = UUID.randomUUID().toString();

    @Before
    public void init(){
        client = new RequestClient( );
        client.setClientId("test_dicom_manager");
        client.setClientSecret("test_dicom_manager");
    }

    @Test
    public void testAccountManagementLifeCycle( ) throws IdentityAccountException {

        // Create account
        System.out.println( "Create new account with login " + mail + " and password " + initialPassword );
        final ChangeAccountResponse createAccountResponse = accountManagementService.createAccount(mail, initialPassword, client);
        if(createAccountResponse.getResult() != null){
            final String guid = createAccountResponse.getResult().getUid();
            System.out.println( "Created account with GUID " + guid + " and status " + createAccountResponse );

            // Get account
            System.out.println( "Get created account" );
            final GetAccountResponse getAccountResponse = accountManagementService.getAccount(guid, client);
            if(getAccountResponse.getResult() != null){
                final AccountDto accountDto = getAccountResponse.getResult();
                System.out.println( "Got account " + accountDto + " with status " + getAccountResponse );

                // Modify account
                System.out.println( "Modify account to set validated flag to true" );
                accountDto.setValidated("true");
                final ChangeAccountResponse modifyAccountResponse = accountManagementService.modifyAccount(accountDto, client);
                System.out.println( "Modified account with GUID " + guid + " and status " + modifyAccountResponse );

                // Get account to ensure modification is done
                System.out.println( "Get modified account" );
                final GetAccountResponse getModifiedAccountResponse = accountManagementService.getAccount(guid, client);
                if(getModifiedAccountResponse.getResult() != null){
                    final AccountDto modifiedAccount = getModifiedAccountResponse.getResult();
                    System.out.println( "Got modified account " + modifiedAccount + " with status " + getModifiedAccountResponse );

                    // Modify password
                    System.out.println( "Modify account password" );
                    final ChangeAccountResponse modifyPasswordResponse = accountManagementService.modifyPassword(guid, initialPassword, newPassword, client);
                    System.out.println( "Modified account password " + modifyPasswordResponse);

                    // Get account to ensure modification is done
                    System.out.println( "Get password modified account" );
                    final GetAccountResponse getModifiedPasswordAccountResponse = accountManagementService.getAccount(guid, client);
                    if(getModifiedPasswordAccountResponse.getResult() != null){
                        final AccountDto modifiedPasswordAccount = getModifiedPasswordAccountResponse.getResult();
                        System.out.println( "Got password modified account " + modifiedPasswordAccount + " with status " + getModifiedPasswordAccountResponse );

                        System.out.println( "Get keycloack account" );
                        final GetAccountResponse getKeycloackAccountResponse = accountManagementService.getKeyclockAccount(guid, client);
                        if(getKeycloackAccountResponse.getResult() != null){
                            final AccountDto keyclockAccount = getKeycloackAccountResponse.getResult();
                            System.out.println( "Got Keycloack account " + keyclockAccount + " with status " + getKeycloackAccountResponse );
                        }

                        //Get password history
                        System.out.println( "Get account password history" );
                        final SearchListPasswordHistortyResponse passwordHistory = passwordHistoryService.getListPasswordHistory(guid, client);
                        System.out.println( "Got account password history " + passwordHistory);

                        System.out.println( "Search list of accounts by GUID" );
                        final SearchListAccountResponse searchListAccountByGuidResponse = accountManagementService.getAccountListByGuid(List.of(guid), client);
                        if(searchListAccountByGuidResponse.getResult() != null){
                            final SearchListAccountResult acountsByGuid = searchListAccountByGuidResponse.getResult();
                            System.out.println( "Got list of accounts " + acountsByGuid + " with status " + searchListAccountByGuidResponse );
                        }

                        System.out.println( "Search list of accounts by mail" );
                        final SearchListAccountResponse searchListAccountResponse = accountManagementService.getAccountList(mail, null, null, null, null, client);
                        if(searchListAccountResponse.getResult() != null){
                            final SearchListAccountResult acountsByMail = searchListAccountResponse.getResult();
                            System.out.println( "Got list of accounts " + acountsByMail + " with status " + searchListAccountResponse);
                        }

                        // Clean data
                        System.out.println( "Delete account " + guid );
                        final ChangeAccountResponse deleteAccountResponse = accountManagementService.deleteAccount(guid, client);
                        System.out.println( "Deleted account with GUID " + guid + " and status " + deleteAccountResponse );
                    }
                }
            }
        } else {
            System.out.println( "Could not create account : " + createAccountResponse );
        }
    }

    @Test
    public void testFederationLinkManagementService() throws IdentityAccountException {
        // Create account
        System.out.println( "Create new account with login " + mail + " and password " + initialPassword );
        final ChangeAccountResponse createAccountResponse = accountManagementService.createAccount(mail, initialPassword, client);
        if(createAccountResponse.getResult() != null){
            final String guid = createAccountResponse.getResult().getUid();
            System.out.println( "Created account with GUID " + guid + " and status " + createAccountResponse );

            // Try to get an existing link
            System.out.println("Try to get an existing link");
            final SearchListFederationLinkResponse federationLinkListResponse = federationLinkManagementService.getFederationLinkList(guid, client);
            System.out.println( "Got link for " + guid + " and status " + federationLinkListResponse );

            if(federationLinkListResponse.getResult() != null && federationLinkListResponse.getResult().getFederationLinks() != null && !federationLinkListResponse.getResult().getFederationLinks().isEmpty()){
                System.out.println("Existing link found !");
            } else {
                System.out.println("No link found.. Create one...");
                final FederationLinkDto federationLink = new FederationLinkDto();
                federationLink.setGuid(guid);
                federationLink.setIdentityId(UUID.randomUUID().toString());
                federationLink.setIdentityName(UUID.randomUUID().toString());
                federationLink.setIdentityProvider("FranceConnect");
                final ChangeFederationLinkResponse createFederationLinkResponse = federationLinkManagementService.createFederationLink(federationLink, client);
                System.out.println( "Created federation link for GUID " + guid + " and status " + createFederationLinkResponse );
                if(!Objects.equals(createFederationLinkResponse.getStatus(), "ERROR")){
                    final ChangeFederationLinkResponse deleteFederationLinkResponse = federationLinkManagementService.deleteFederationLink(guid, "identitystore-tests", client);
                    System.out.println( "Delted federation link for GUID " + guid + " and status " + deleteFederationLinkResponse );
                }
            }

            // Clean data
            System.out.println( "Delete account " + guid );
            final ChangeAccountResponse deleteAccountResponse = accountManagementService.deleteAccount(guid, client);
            System.out.println( "Deleted account with GUID " + guid + " and status " + deleteAccountResponse );
        } else {
            System.out.println( "Could not create account : " + createAccountResponse );
        }
    }
}
