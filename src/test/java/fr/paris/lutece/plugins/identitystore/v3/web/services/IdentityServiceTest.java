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

import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AttributeDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AttributeTreatmentType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.IdentityDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.ResponseStatusType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.crud.IdentityChangeRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.crud.IdentityChangeResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.exporting.IdentityExportRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.exporting.IdentityExportResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.merge.IdentityMergeRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.merge.IdentityMergeResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.search.IdentitySearchRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.search.IdentitySearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.search.SearchAttribute;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.search.SearchDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.service.IdentityService;
import fr.paris.lutece.plugins.identitystore.v3.web.service.IdentityServiceExtended;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

/**
 * test of NotificationService
 */
public class IdentityServiceTest extends AbstractServiceTest
{
    @Resource( name = "identityService.rest.httpAccess" )
    private IdentityService service;

    @Resource( name = "identityServiceExtended.rest.httpAccess" )
    private IdentityServiceExtended extendedService;

    @Test
    public void testUpdate( ) throws IdentityStoreException
    {
        final IdentitySearchResponse response = service.getIdentity( cuid, clientCode, getRequestAuthor( ) );
        assert ResponseStatusType.OK.equals( response.getStatus( ).getType( ) ) && !response.getIdentities( ).isEmpty( );
        final IdentityDto searchedIdentity = response.getIdentities( ).get( 0 );

        final IdentityChangeRequest updateRequest = new IdentityChangeRequest( );
        final IdentityDto identity = new IdentityDto( );
        updateRequest.setIdentity( identity );
        final AttributeDto attributeDto = new AttributeDto( );
        identity.getAttributes( ).add( attributeDto );
        attributeDto.setCertifier( "fccertifier" );
        attributeDto.setCertificationDate( new Date( ) );
        attributeDto.setKey( Constants.PARAM_PREFERRED_USERNAME );
        attributeDto.setValue( "Tata" );
        identity.setLastUpdateDate( searchedIdentity.getLastUpdateDate( ) );

        final IdentityChangeResponse identityChangeResponse = service.updateIdentity( searchedIdentity.getCustomerId( ), updateRequest, clientCode,
                this.getRequestAuthor( ) );
        assert identityChangeResponse.getStatus( ).getHttpCode( ) == 201; // equivalent to status == SUCCESS || status == INCOMPLETE_SUCCESS
    }

    @Test
    public void testMerge( ) throws IdentityStoreException
    {
        final IdentitySearchResponse response = service.getIdentity( cuid, clientCode, getRequestAuthor( ) );
        assert ResponseStatusType.OK.equals( response.getStatus( ).getType( ) ) && !response.getIdentities( ).isEmpty( );
        final IdentityDto firstidentity = response.getIdentities( ).get( 0 );

        final IdentityChangeRequest createRequest = new IdentityChangeRequest( );
        createRequest.setIdentity( this.getBasicIdentity( ) );

        final IdentityChangeResponse createResponse = service.createIdentity( createRequest, clientCode, this.getRequestAuthor( ) );
        assert ( ResponseStatusType.SUCCESS.equals( createResponse.getStatus( ).getType( ) )
                || ResponseStatusType.INCOMPLETE_SUCCESS.equals( createResponse.getStatus( ).getType( ) ) )
                && StringUtils.isNotEmpty( createResponse.getCustomerId( ) );

        final IdentitySearchResponse searchResponse = service.getIdentity( createResponse.getCustomerId( ), clientCode, getRequestAuthor( ) );
        assert ResponseStatusType.OK.equals( searchResponse.getStatus( ).getType( ) ) && !searchResponse.getIdentities( ).isEmpty( );
        final IdentityDto secondIdentity = searchResponse.getIdentities( ).get( 0 );

        final IdentityMergeRequest mergeRequest = new IdentityMergeRequest( );
        mergeRequest.setPrimaryCuid( firstidentity.getCustomerId( ) );
        mergeRequest.setSecondaryCuid( secondIdentity.getCustomerId( ) );

        mergeRequest.setPrimaryLastUpdateDate( firstidentity.getLastUpdateDate( ) );
        mergeRequest.setSecondaryLastUpdateDate( secondIdentity.getLastUpdateDate( ) );

        final IdentityMergeResponse identityMergeResponse = extendedService.mergeIdentities( mergeRequest, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( identityMergeResponse.getStatus( ).getType( ), ResponseStatusType.SUCCESS );
    }

    @Test
    public void testUnMerge( ) throws IdentityStoreException
    {
        /* Get the first identity */
        final IdentitySearchResponse response = service.getIdentity( cuid, clientCode, getRequestAuthor( ) );
        assert ResponseStatusType.OK.equals( response.getStatus( ).getType( ) ) && !response.getIdentities( ).isEmpty( );
        final IdentityDto firstIdentity = response.getIdentities( ).get( 0 );

        /* Create a second identity */
        final IdentityChangeRequest createRequest = new IdentityChangeRequest( );
        createRequest.setIdentity( this.getBasicIdentity( ) );

        final IdentityChangeResponse createResponse = service.createIdentity( createRequest, clientCode, this.getRequestAuthor( ) );
        assert ( ResponseStatusType.SUCCESS.equals( createResponse.getStatus( ).getType( ) )
                || ResponseStatusType.INCOMPLETE_SUCCESS.equals( createResponse.getStatus( ).getType( ) ) )
                && StringUtils.isNotEmpty( createResponse.getCustomerId( ) );
        final String secondaryCustomerId = createResponse.getCustomerId( );

        /* Search the created identity */
        final IdentitySearchResponse searchResponse = service.getIdentity( secondaryCustomerId, clientCode, getRequestAuthor( ) );
        assert ResponseStatusType.OK.equals( response.getStatus( ).getType( ) ) && !searchResponse.getIdentities( ).isEmpty( );
        final IdentityDto secondIdentity = searchResponse.getIdentities( ).get( 0 );

        /* Merge second identity to first */
        final IdentityMergeRequest mergeRequest = new IdentityMergeRequest( );
        mergeRequest.setPrimaryCuid( cuid );
        mergeRequest.setSecondaryCuid( secondaryCustomerId );

        mergeRequest.setPrimaryLastUpdateDate( firstIdentity.getLastUpdateDate( ) );
        mergeRequest.setSecondaryLastUpdateDate( secondIdentity.getLastUpdateDate( ) );

        final IdentityMergeResponse identityMergeResponse = extendedService.mergeIdentities( mergeRequest, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( identityMergeResponse.getStatus( ).getType( ), ResponseStatusType.SUCCESS );

        /* Identities have been updated so request it again in order to have the last update date */
        final IdentitySearchResponse getUpdatedFirstIdentity = service.getIdentity( cuid, clientCode, getRequestAuthor( ) );
        assert Objects.equals( getUpdatedFirstIdentity.getStatus( ).getType( ), ResponseStatusType.OK ) && !getUpdatedFirstIdentity.getIdentities( ).isEmpty( );

        final IdentitySearchResponse getUpdatedSecondIdentity = service.getIdentity( secondaryCustomerId, clientCode, getRequestAuthor( ) );
        assert Objects.equals( getUpdatedSecondIdentity.getStatus( ).getType( ), ResponseStatusType.OK )
                && !getUpdatedSecondIdentity.getIdentities( ).isEmpty( );

        /* Cancel the previous merge */
        final IdentityMergeRequest unMergeRequest = new IdentityMergeRequest( );

        final IdentityDto updatedFirstIdentity = getUpdatedFirstIdentity.getIdentities( ).get( 0 );
        unMergeRequest.setPrimaryCuid( cuid );
        unMergeRequest.setPrimaryLastUpdateDate( updatedFirstIdentity.getLastUpdateDate( ) );

        final IdentityDto updatedSecondaryIdentity = getUpdatedSecondIdentity.getIdentities( ).get( 0 );
        unMergeRequest.setSecondaryCuid( secondaryCustomerId );
        unMergeRequest.setSecondaryLastUpdateDate( updatedSecondaryIdentity.getLastUpdateDate( ) );

        final IdentityMergeResponse unmergeResponse = extendedService.unMergeIdentities( unMergeRequest, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( unmergeResponse.getStatus( ).getType( ), ResponseStatusType.SUCCESS );

        /* Request the secondary identity, so we can check that it has no attribute left, and is not merged anymore */
        // TODO il n'y a pas d'API permettant de récupérer une identité merged, on renvoie systématiquement le master => impossible d'unmerge
        final IdentitySearchResponse getUnmergedIdentity = service.getIdentity( secondaryCustomerId, clientCode, getRequestAuthor( ) );
        final IdentityDto unmergedSecondaryIdentity = getUnmergedIdentity.getIdentities( ).get( 0 );
        assert getUnmergedIdentity.getStatus( ).getType( ) == ResponseStatusType.OK && !getUnmergedIdentity.getIdentities( ).isEmpty( )
                && unmergedSecondaryIdentity.getAttributes( ).isEmpty( )
                && ( unmergedSecondaryIdentity.getMerge( ) == null || !unmergedSecondaryIdentity.getMerge( ).isMerged( ) );

        final IdentityChangeResponse identityChangeResponse = service.deleteIdentity( secondaryCustomerId, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( identityChangeResponse.getStatus( ).getType( ), ResponseStatusType.SUCCESS );
    }

    @Test
    public void testDelete( ) throws IdentityStoreException
    {
        final IdentitySearchResponse response = service.getIdentity( cuid, clientCode, getRequestAuthor( ) );
        assert ResponseStatusType.OK.equals( response.getStatus( ).getType( ) ) && !response.getIdentities( ).isEmpty( );

        final IdentityChangeRequest createRequest = new IdentityChangeRequest( );
        createRequest.setIdentity( this.getBasicIdentity( ) );

        final IdentityChangeResponse createResponse = service.createIdentity( createRequest, clientCode, this.getRequestAuthor( ) );
        assert ( ResponseStatusType.SUCCESS.equals( createResponse.getStatus( ).getType( ) )
                || ResponseStatusType.INCOMPLETE_SUCCESS.equals( createResponse.getStatus( ).getType( ) ) )
                && StringUtils.isNotEmpty( createResponse.getCustomerId( ) );

        final IdentityChangeResponse identityChangeResponse = service.deleteIdentity( createResponse.getCustomerId( ), clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( identityChangeResponse.getStatus( ).getType( ), ResponseStatusType.SUCCESS );

        final IdentitySearchResponse deletedIdentity = service.getIdentity( createResponse.getCustomerId( ), clientCode, getRequestAuthor( ) );
        assert Objects.equals( deletedIdentity.getStatus( ).getType( ), ResponseStatusType.OK ) && !deletedIdentity.getIdentities( ).isEmpty( )
                && deletedIdentity.getIdentities( ).get( 0 ).getExpiration( ).isDeleted( );
    }

    @Test
    public void testUncertify( ) throws IdentityStoreException
    {
        final IdentityChangeRequest createRequest = new IdentityChangeRequest( );
        createRequest.setIdentity( this.getBasicIdentity( ) );

        final IdentityChangeResponse createResponse = service.createIdentity( createRequest, clientCode, this.getRequestAuthor( ) );
        assert ( ResponseStatusType.SUCCESS.equals( createResponse.getStatus( ).getType( ) )
                || ResponseStatusType.INCOMPLETE_SUCCESS.equals( createResponse.getStatus( ).getType( ) ) )
                && StringUtils.isNotEmpty( createResponse.getCustomerId( ) );

        final String createdCustomerId = createResponse.getCustomerId( );
        final IdentityChangeResponse identityChangeResponse = extendedService.uncertifyIdentity( createdCustomerId, clientCode, this.getRequestAuthor( ) );
        assert identityChangeResponse.getStatus( ).getType( ) == ResponseStatusType.SUCCESS;

        final IdentitySearchResponse getUncertifiedIdentity = service.getIdentity( createdCustomerId, clientCode, getRequestAuthor( ) );
        assert getUncertifiedIdentity.getStatus( ).getType( ) == ResponseStatusType.OK && !getUncertifiedIdentity.getIdentities( ).isEmpty( )
                && getUncertifiedIdentity.getIdentities( ).get( 0 ).getAttributes( ).stream( )
                        .allMatch( a -> a.getCertifier( ) != null && a.getCertifier( ).equals( "DEC" ) );
    }

    @Test
    public void testExport( ) throws IdentityStoreException
    {
        final IdentityExportRequest request = new IdentityExportRequest( );
        request.getCuidList( ).add( cuid );
        request.getAttributeKeyList( ).addAll( Arrays.asList( Constants.PARAM_GENDER, Constants.PARAM_FIRST_NAME, Constants.PARAM_FAMILY_NAME ) );

        final IdentityExportResponse response = service.exportIdentities( request, clientCode, this.getRequestAuthor( ) );
        assert response.getStatus( ).getType( ) == ResponseStatusType.OK && response.getIdentities( ).size( ) == 1
                && response.getIdentities( ).get( 0 ).getAttributes( ).size( ) == 3 && response.getIdentities( ).get( 0 ).getAttributes( ).stream( )
                        .filter( a -> request.getAttributeKeyList( ).contains( a.getKey( ) ) ).count( ) == 3;
    }

    @Test
    public void testFullAPI( ) throws IdentityStoreException
    {
        final IdentitySearchResponse getFirstIdentityResponse = service.getIdentity( cuid, clientCode, getRequestAuthor( ) );
        assert getFirstIdentityResponse.getStatus( ).getType( ) == ResponseStatusType.OK
                && Objects.equals( getFirstIdentityResponse.getIdentities( ).get( 0 ).getCustomerId( ), cuid );
        final IdentityDto firstIdentity = getFirstIdentityResponse.getIdentities( ).get( 0 );

        final IdentityDto newIdentity = this.getBasicIdentity( );
        final AttributeDto email = new AttributeDto( );
        email.setCertifier( "MAIL" );
        email.setCertificationDate( new Date( ) );
        email.setKey( Constants.PARAM_EMAIL );
        final String secondIdentityEmailValue = "692433.xxxxx48@xxxxaxx.xx";
        email.setValue( secondIdentityEmailValue );
        newIdentity.getAttributes( ).add( email );

        final IdentityChangeRequest createRequest = new IdentityChangeRequest( );
        createRequest.setIdentity( newIdentity );

        final IdentityChangeResponse createSecondIdentityResponse = service.createIdentity( createRequest, clientCode, this.getRequestAuthor( ) );
        assert createSecondIdentityResponse != null
                && ( createSecondIdentityResponse.getStatus( ).getType( ) == ResponseStatusType.SUCCESS
                        || createSecondIdentityResponse.getStatus( ).getType( ) == ResponseStatusType.INCOMPLETE_SUCCESS )
                && StringUtils.isNotEmpty( createSecondIdentityResponse.getCustomerId( ) );

        final IdentitySearchResponse getSecondIdentityResponse = service.getIdentity( createSecondIdentityResponse.getCustomerId( ), clientCode,
                getRequestAuthor( ) );
        assert getSecondIdentityResponse != null && getSecondIdentityResponse.getStatus( ).getType( ) == ResponseStatusType.OK
                && Objects.equals( getSecondIdentityResponse.getIdentities( ).get( 0 ).getCustomerId( ), createSecondIdentityResponse.getCustomerId( ) );
        final IdentityDto secondIdentity = getSecondIdentityResponse.getIdentities( ).get( 0 );

        final IdentitySearchRequest secondIdentitySearchRequest = new IdentitySearchRequest( );
        final SearchDto search = new SearchDto( );
        secondIdentitySearchRequest.setSearch( search );
        final SearchAttribute emailSearchAttribute = new SearchAttribute( );
        search.getAttributes( ).add( emailSearchAttribute );
        emailSearchAttribute.setTreatmentType( AttributeTreatmentType.STRICT );
        emailSearchAttribute.setValue( secondIdentityEmailValue );
        emailSearchAttribute.setKey( Constants.PARAM_EMAIL );
        final IdentitySearchResponse searchSecondIdentityResponse = service.searchIdentities( secondIdentitySearchRequest, clientCode,
                this.getRequestAuthor( ) );
        assert searchSecondIdentityResponse != null && searchSecondIdentityResponse.getStatus( ).getType( ) == ResponseStatusType.OK;

        final IdentityMergeRequest mergeRequest = new IdentityMergeRequest( );
        mergeRequest.setPrimaryCuid( firstIdentity.getCustomerId( ) );
        mergeRequest.setSecondaryCuid( secondIdentity.getCustomerId( ) );

        mergeRequest.setPrimaryLastUpdateDate( firstIdentity.getLastUpdateDate( ) );
        mergeRequest.setSecondaryLastUpdateDate( secondIdentity.getLastUpdateDate( ) );

        final IdentityMergeResponse identityMergeResponse = extendedService.mergeIdentities( mergeRequest, clientCode, this.getRequestAuthor( ) );
        assert identityMergeResponse != null && Objects.equals( identityMergeResponse.getStatus( ).getType( ), ResponseStatusType.SUCCESS );
    }

    private IdentityDto getBasicIdentity( )
    {
        final IdentityDto identity = new IdentityDto( );

        final AttributeDto firstName = new AttributeDto( );
        firstName.setCertifier( "DEC" );
        firstName.setCertificationDate( new Date( ) );
        firstName.setKey( Constants.PARAM_FIRST_NAME );
        firstName.setValue( "Toto" );
        identity.getAttributes( ).add( firstName );

        final AttributeDto lastName = new AttributeDto( );
        lastName.setCertifier( "DEC" );
        lastName.setCertificationDate( new Date( ) );
        lastName.setKey( Constants.PARAM_FAMILY_NAME );
        lastName.setValue( "Toto" );
        identity.getAttributes( ).add( lastName );

        final AttributeDto birthDate = new AttributeDto( );
        birthDate.setCertifier( "DEC" );
        birthDate.setCertificationDate( new Date( ) );
        birthDate.setKey( Constants.PARAM_BIRTH_DATE );
        birthDate.setValue( "01/01/1901" );
        identity.getAttributes( ).add( birthDate );
        return identity;
    }
}
