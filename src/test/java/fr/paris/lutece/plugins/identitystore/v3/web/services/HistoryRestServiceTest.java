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

import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AttributeTreatmentType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.ResponseStatusType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.history.AttributeChangeType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.history.IdentityChangeType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.history.IdentityHistoryGetResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.history.IdentityHistorySearchRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.history.IdentityHistorySearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.search.SearchUpdatedAttribute;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.search.UpdatedIdentitySearchRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.search.UpdatedIdentitySearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.service.IdentityService;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * test of NotificationService
 */
public class HistoryRestServiceTest extends AbstractServiceTest
{
    @Resource( name = "identityService.rest.httpAccess" )
    private IdentityService service;

    @Test
    public void testGetIdentityHistory( ) throws IdentityStoreException
    {
        // Search all modifications on Identity cuid
        final IdentityHistoryGetResponse response = service.getIdentityHistory( cuid, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( response.getStatus( ).getType( ), ResponseStatusType.OK ) && response.getHistory( ) != null
                && !response.getHistory( ).getAttributeHistories( ).isEmpty( );
    }

    @Test
    public void testGetUpdatedIdentities( ) throws IdentityStoreException
    {
        // Get all updated or created identities since 3000 days ago
        final UpdatedIdentitySearchRequest requestWithDays = new UpdatedIdentitySearchRequest( );
        requestWithDays.setDays( 3000 );
        final UpdatedIdentitySearchResponse responseWithDays = service.getUpdatedIdentities( requestWithDays, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( responseWithDays.getStatus( ).getType( ), ResponseStatusType.OK ) && !responseWithDays.getUpdatedIdentityList( ).isEmpty( );

        // Get all consolidated identities
        final UpdatedIdentitySearchRequest requestWithType = new UpdatedIdentitySearchRequest( );
        requestWithType.getIdentityChangeTypes( ).add( IdentityChangeType.CONSOLIDATED );
        final UpdatedIdentitySearchResponse responseWithType = service.getUpdatedIdentities( requestWithType, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( responseWithType.getStatus( ).getHttpCode(), 200 ) ;

        // Get all newly created identities since 1 year ago
        final UpdatedIdentitySearchRequest requestWithDateAndType = new UpdatedIdentitySearchRequest( );
        requestWithDateAndType.setDays( 365 );
        requestWithDateAndType.getIdentityChangeTypes( ).add( IdentityChangeType.CREATE );
        final UpdatedIdentitySearchResponse responseWithDaysAndType = service.getUpdatedIdentities( requestWithDateAndType, clientCode,
                this.getRequestAuthor( ) );
        assert Objects.equals( responseWithDaysAndType.getStatus( ).getType( ), ResponseStatusType.OK )
                && !responseWithDaysAndType.getUpdatedIdentityList( ).isEmpty( );

        // Get all identities that had their email updated since 2 years ago
        final UpdatedIdentitySearchRequest requestWithDateAndAttribute = new UpdatedIdentitySearchRequest( );
        requestWithDateAndAttribute.setDays( 730 );
        final SearchUpdatedAttribute attribute = new SearchUpdatedAttribute( );
        attribute.setAttributeKey( "email" );
        attribute.getAttributeChangeTypes( ).add( AttributeChangeType.UPDATE );
        requestWithDateAndAttribute.getUpdatedAttributes( ).add( attribute );
        final UpdatedIdentitySearchResponse responseWithDateAndAttribute = service.getUpdatedIdentities( requestWithDateAndAttribute, clientCode,
                this.getRequestAuthor( ) );
        assert Objects.equals( responseWithDateAndAttribute.getStatus( ).getType( ), ResponseStatusType.OK )
                && !responseWithDateAndAttribute.getUpdatedIdentityList( ).isEmpty( );
    }

    @Test
    public void testSearchIdentityHistory( ) throws IdentityStoreException
    {

        // Search all modifications with STRICT matches
        //TODO test when refactoring of history API is done
//        final IdentityHistorySearchRequest requestWithMetadata2 = new IdentityHistorySearchRequest( );
//        requestWithMetadata2.getMetadata( ).put( AttributeTreatmentType.STRICT.name( ), "birthdate,birthplace_code" );
//        requestWithMetadata2.getMetadata( ).put( Constants.METADATA_DUPLICATE_RULE_CODE, "RG_GEN_StrictDoublon_01" );
//        requestWithMetadata2.setNbDaysFrom( 15 );
//        final IdentityHistorySearchResponse responseWithMetadata2 = service.searchIdentityHistory( requestWithMetadata2, clientCode, this.getRequestAuthor( ) );
//        assert Objects.equals( responseWithMetadata2.getStatus( ).getType( ), ResponseStatusType.OK );

        // Search all modifications on Identity cuid
        final IdentityHistorySearchRequest requestByCuid = new IdentityHistorySearchRequest( );
        requestByCuid.setCustomerId( cuid );
        final IdentityHistorySearchResponse responseByCuid = service.searchIdentityHistory( requestByCuid, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( responseByCuid.getStatus( ).getType( ), ResponseStatusType.OK );

        // Search all modifications triggered by rule RG_GEN_SuspectDoublon_08 within 15 last days
        //TODO test when refactoring of history API is done
//        final IdentityHistorySearchRequest requestWithMetadata = new IdentityHistorySearchRequest( );
//        requestWithMetadata.getMetadata( ).put( Constants.METADATA_DUPLICATE_RULE_CODE, duplicateRuleCode );
//        requestWithMetadata.setNbDaysFrom( 15 );
//        final IdentityHistorySearchResponse responseWithMetadata = service.searchIdentityHistory( requestWithMetadata, clientCode, this.getRequestAuthor( ) );
//        assert Objects.equals( responseWithMetadata.getStatus( ).getType( ), ResponseStatusType.OK );

        // Search all modifications performed by author Imaginary
        final IdentityHistorySearchRequest requestByAuthor = new IdentityHistorySearchRequest( );
        requestByAuthor.setAuthorName( "teSt@gmail.fr" );
        final IdentityHistorySearchResponse responseByAuthor = service.searchIdentityHistory( requestByAuthor, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( responseByAuthor.getStatus( ).getType( ), ResponseStatusType.OK );

        // Search all modifications of type
        final IdentityHistorySearchRequest requestByType = new IdentityHistorySearchRequest( );
        requestByType.setIdentityChangeType( IdentityChangeType.CONSOLIDATED );
        final IdentityHistorySearchResponse responseByType = service.searchIdentityHistory( requestByType, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( responseByType.getStatus( ).getType( ), ResponseStatusType.OK );

        // Search all modifications of type
        final IdentityHistorySearchRequest requestByClientCode = new IdentityHistorySearchRequest( );
        requestByClientCode.setClientCode( "testo" );
        final IdentityHistorySearchResponse responseByClientCode = service.searchIdentityHistory( requestByClientCode, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( responseByClientCode.getStatus( ).getType( ), ResponseStatusType.OK );
    }
}
