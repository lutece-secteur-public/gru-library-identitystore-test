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

import fr.paris.lutece.plugins.accountgenerator.web.service.AccountGeneratorService;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.account.RequestClient;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.account.generator.AccountGenerationDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.account.generator.AccountGenerationRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.account.generator.AccountGenerationResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.ResponseStatusFactory;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * test of NotificationService
 */

public class AccountGeneratorServiceTest extends AbstractServiceTest
{
    @Value( "${client.code}" )
    protected String clientCode;

    @Resource( name = "identityService.accountgenerator.httpAccess" )
    private AccountGeneratorService accountGeneratorService;

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
    public void testAccountManagementLifeCycle( ) throws IdentityStoreException {

        // Create account
        System.out.println( "Create new account with login " + mail + " and password " + initialPassword );
        final AccountGenerationRequest request = new AccountGenerationRequest( );
        final AccountGenerationDto accountGeneration = new AccountGenerationDto();
        accountGeneration.setBatchSize(2);
        accountGeneration.setGenerateAccount(true);
        accountGeneration.setGenerationIncrementOffset(0);
        accountGeneration.setGenerationPattern("pattern");
        accountGeneration.setNbDaysOfValidity(300);
        request.setAccountGenerationDto(accountGeneration);
        final AccountGenerationResponse response = accountGeneratorService.createAccountBatch(request, this.getRequestAuthor(),clientCode);
        if(response.getStatus().getHttpCode() == ResponseStatusFactory.success().getHttpCode()){
            System.out.println( "Created accounts : " + response );
        } else {
            System.out.println( "Could not create account : " + response );
        }
    }
}
