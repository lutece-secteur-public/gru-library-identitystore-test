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
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.search.IdentitySearchRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.search.IdentitySearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.search.SearchAttribute;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.search.SearchDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.service.IdentityService;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Objects;

/**
 * test of NotificationService
 */
public class SearchIdentityServiceTest extends AbstractServiceTest
{
    @Resource( name = "identityService.rest.httpAccess" )
    private IdentityService service;

    @Value( "${search.birth.date}" )
    protected String birthdate;

    @Value( "${search.family.name}" )
    protected String familyName;

    @Value( "${search.first.name}" )
    protected String firstName;

    @Value( "${search.login}" )
    protected String login;

    @Value( "${search.email}" )
    protected String email;

    @Test
    public void testGetIdentity( ) throws IdentityStoreException
    {
        final IdentitySearchResponse getResponse = service.getIdentity( cuid, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( getResponse.getStatus( ).getType( ), ResponseStatusType.OK );
    }

    @Test
    public void testGetIdentityByCuid( ) throws IdentityStoreException
    {
        final IdentitySearchResponse searchResponse = service.getIdentityByCustomerId( cuid, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( searchResponse.getStatus( ).getType( ), ResponseStatusType.OK ) && searchResponse.getIdentities( ).size( ) == 1;
    }

    @Test
    public void testGetIdentityByConnectionId( ) throws IdentityStoreException
    {
        final IdentitySearchResponse searchResponse = service.getIdentityByConnectionId( guid, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( searchResponse.getStatus( ).getType( ), ResponseStatusType.OK ) && searchResponse.getIdentities( ).size( ) == 1;
    }

    @Test
    public void testSearchIdentityFailBecauseRequiredParams( ) throws IdentityStoreException
    {
        final IdentitySearchRequest identitySearchRequest = new IdentitySearchRequest( );
        identitySearchRequest.setMax( 2 );
        final SearchDto search = new SearchDto( );
        search.setAttributes( new ArrayList<>( ) );
        final SearchAttribute searchAttribute = new SearchAttribute( );
        searchAttribute.setValue( familyName );
        searchAttribute.setKey( Constants.PARAM_FAMILY_NAME );
        searchAttribute.setTreatmentType( AttributeTreatmentType.APPROXIMATED );
        search.getAttributes( ).add( searchAttribute );
        identitySearchRequest.setSearch( search );
        final IdentitySearchResponse getResponse = service.searchIdentities( identitySearchRequest, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( getResponse.getStatus( ).getType( ), ResponseStatusType.FAILURE );
    }

    @Test
    public void testSearchIdentityWithNamesAndBirthDate( ) throws IdentityStoreException
    {
        final IdentitySearchRequest identitySearchRequest = new IdentitySearchRequest( );
        identitySearchRequest.setMax( 2 );
        final SearchDto search = new SearchDto( );
        search.setAttributes( new ArrayList<>( ) );
        final SearchAttribute familyNameSearchAttribute = new SearchAttribute( );
        familyNameSearchAttribute.setValue( familyName );
        familyNameSearchAttribute.setKey( Constants.PARAM_FAMILY_NAME );
        familyNameSearchAttribute.setTreatmentType( AttributeTreatmentType.APPROXIMATED );
        search.getAttributes( ).add( familyNameSearchAttribute );
        final SearchAttribute firstNameSearchAttribute = new SearchAttribute( );
        firstNameSearchAttribute.setValue( firstName );
        firstNameSearchAttribute.setKey( Constants.PARAM_FIRST_NAME );
        firstNameSearchAttribute.setTreatmentType( AttributeTreatmentType.APPROXIMATED );
        search.getAttributes( ).add( firstNameSearchAttribute );
        final SearchAttribute birthdateSearchAttribute = new SearchAttribute( );
        birthdateSearchAttribute.setValue( birthdate );
        birthdateSearchAttribute.setKey( Constants.PARAM_BIRTH_DATE );
        birthdateSearchAttribute.setTreatmentType( AttributeTreatmentType.APPROXIMATED );
        search.getAttributes( ).add( birthdateSearchAttribute );
        identitySearchRequest.setSearch( search );
        final IdentitySearchResponse searchResponse = service.searchIdentities( identitySearchRequest, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( searchResponse.getStatus( ).getType( ), ResponseStatusType.OK );
    }

    @Test
    public void testSearchIdentityWithEmail( ) throws IdentityStoreException
    {
        final IdentitySearchRequest identitySearchRequest = new IdentitySearchRequest( );
        identitySearchRequest.setMax( 2 );
        final SearchDto search = new SearchDto( );
        search.setAttributes( new ArrayList<>( ) );
        final SearchAttribute familyNameSearchAttribute = new SearchAttribute( );
        familyNameSearchAttribute.setValue( email );
        familyNameSearchAttribute.setKey( Constants.PARAM_EMAIL );
        familyNameSearchAttribute.setTreatmentType( AttributeTreatmentType.STRICT );
        search.getAttributes( ).add( familyNameSearchAttribute );
        identitySearchRequest.setSearch( search );
        final IdentitySearchResponse searchResponse = service.searchIdentities( identitySearchRequest, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( searchResponse.getStatus( ).getType( ), ResponseStatusType.OK );
    }

    @Test
    public void testSearchIdentityWithLogin( ) throws IdentityStoreException
    {
        final IdentitySearchRequest identitySearchRequest = new IdentitySearchRequest( );
        identitySearchRequest.setMax( 2 );
        final SearchDto search = new SearchDto( );
        search.setAttributes( new ArrayList<>( ) );
        final SearchAttribute familyNameSearchAttribute = new SearchAttribute( );
        familyNameSearchAttribute.setValue( login );
        familyNameSearchAttribute.setKey( Constants.PARAM_LOGIN );
        familyNameSearchAttribute.setTreatmentType( AttributeTreatmentType.STRICT );
        search.getAttributes( ).add( familyNameSearchAttribute );
        identitySearchRequest.setSearch( search );
        final IdentitySearchResponse searchResponse = service.searchIdentities( identitySearchRequest, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( searchResponse.getStatus( ).getType( ), ResponseStatusType.OK );
    }
}
