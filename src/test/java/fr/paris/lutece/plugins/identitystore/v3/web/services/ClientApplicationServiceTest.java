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

import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.application.ClientApplicationDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.application.ClientChangeResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.application.ClientSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.application.ClientsSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.ResponseStatusType;
import fr.paris.lutece.plugins.identitystore.v3.web.service.ClientApplicationService;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.UUID;

/**
 * test of NotificationService
 */

public class ClientApplicationServiceTest extends AbstractServiceTest
{
    @Resource( name = "identityService.clients.httpAccess" )
    private ClientApplicationService service;

    @Test
    public void testGetAll( ) throws IdentityStoreException
    {
        final ClientsSearchResponse response = service.getClients( applicationCode, clientCode, getRequestAuthor( ) );
        assert Objects.equals( response.getStatus( ).getType( ), ResponseStatusType.OK );
    }

    @Test
    public void testGet( ) throws IdentityStoreException
    {
        final ClientSearchResponse response = service.getClient( clientCode, clientCode, getRequestAuthor( ) );
        assert Objects.equals( response.getStatus( ).getType( ), ResponseStatusType.OK );
    }

    @Test
    public void manageClientApplication( ) throws IdentityStoreException
    {
        final String strTargetClientCode = clientCode + UUID.randomUUID( );

        final ClientApplicationDto clientDto = new ClientApplicationDto( );
        clientDto.setClientCode( strTargetClientCode );
        clientDto.setName( strTargetClientCode );
        clientDto.setApplicationCode( serviceContractsApplicationCode );
        final ClientChangeResponse createResponse = service.createClient( clientDto, clientCode, getRequestAuthor( ) );
        assert Objects.equals( createResponse.getStatus( ).getType( ), ResponseStatusType.SUCCESS )
                && Objects.equals( createResponse.getClientApplication( ).getClientCode( ), strTargetClientCode );

        final ClientApplicationDto createdClient = createResponse.getClientApplication( );
        final String newName = strTargetClientCode + "updated";
        createdClient.setName( newName );

        final ClientChangeResponse updateResponse = service.updateClient( createdClient, clientCode, getRequestAuthor( ) );
        assert Objects.equals( updateResponse.getStatus( ).getType( ), ResponseStatusType.SUCCESS )
                && Objects.equals( updateResponse.getClientApplication( ).getName( ), newName );

    }
}
