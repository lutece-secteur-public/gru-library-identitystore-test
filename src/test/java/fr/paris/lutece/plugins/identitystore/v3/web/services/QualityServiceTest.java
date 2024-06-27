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

import fr.paris.lutece.plugins.identityquality.v3.web.service.IdentityQualityService;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AttributeTreatmentType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.ResponseStatusType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.crud.SuspiciousIdentityChangeRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.crud.SuspiciousIdentityChangeResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.crud.SuspiciousIdentityDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.crud.SuspiciousIdentityExcludeRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.crud.SuspiciousIdentityExcludeResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.crud.SuspiciousIdentitySearchRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.crud.SuspiciousIdentitySearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.duplicate.DuplicateRuleSummarySearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.lock.SuspiciousIdentityLockRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.lock.SuspiciousIdentityLockResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.search.DuplicateSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.search.SearchAttribute;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * test of NotificationService
 */
public class QualityServiceTest extends AbstractServiceTest
{
    @Resource( name = "identityQualityService.rest.httpAccess" )
    private IdentityQualityService service;

    @Value( "${identity.suspicion.cuid}" )
    protected String cuidNoSuspicion;

    @Test
    public void getAllDuplicateRules( ) throws IdentityStoreException
    {
        final DuplicateRuleSummarySearchResponse response = service.getAllDuplicateRules( clientCode, this.getRequestAuthor( ), 5 );
        assert response != null && Objects.equals( response.getStatus( ).getType( ), ResponseStatusType.OK )
                && !response.getDuplicateRuleSummaries( ).isEmpty( );
    }

    @Test
    public void createSuspiciousIdentity( ) throws IdentityStoreException
    {
        final SuspiciousIdentityChangeRequest request = new SuspiciousIdentityChangeRequest( );
        final SuspiciousIdentityDto suspiciousIdentity = new SuspiciousIdentityDto( );
        suspiciousIdentity.setCustomerId( cuidNoSuspicion );
        request.setSuspiciousIdentity( suspiciousIdentity );
        final SuspiciousIdentityChangeResponse response = service.createSuspiciousIdentity( request, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( response.getStatus( ).getType( ), ResponseStatusType.SUCCESS )
                || Objects.equals( response.getStatus( ).getType( ), ResponseStatusType.CONFLICT );
    }

    @Test
    public void testSearchSuspiciousIdentities( ) throws IdentityStoreException
    {
        final SuspiciousIdentitySearchRequest request = new SuspiciousIdentitySearchRequest( );
        request.setRuleCode( duplicateRuleCode );
        final ArrayList<SearchAttribute> attributes = new ArrayList<>( );
        final SearchAttribute searchAttribute = new SearchAttribute( );
        searchAttribute.setKey( Constants.PARAM_BIRTH_COUNTRY_CODE );
        searchAttribute.setValue( "99100" );
        searchAttribute.setTreatmentType( AttributeTreatmentType.STRICT );
        attributes.add( searchAttribute );
        request.setAttributes( attributes );
        final SuspiciousIdentitySearchResponse response = service.getSuspiciousIdentities( request, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( response.getStatus( ).getType( ), ResponseStatusType.OK ) && !response.getSuspiciousIdentities( ).isEmpty( );
    }

    @Test
    public void testPaginatedSearchSuspiciousIdentities( ) throws IdentityStoreException
    {
        final SuspiciousIdentitySearchRequest request = new SuspiciousIdentitySearchRequest( );
        request.setRuleCode( duplicateRuleCode );
        final int max = 30;
        request.setMax( max );
        request.setPage( 1 );
        request.setSize( 5 );
        SuspiciousIdentitySearchResponse response = service.getSuspiciousIdentities( request, clientCode, this.getRequestAuthor( ) );
        final List<SuspiciousIdentityDto> suspiciousIdentities = response.getSuspiciousIdentities( );
        while ( Objects.equals( response.getStatus( ).getType( ), ResponseStatusType.OK ) && response.getPagination( ) != null
                && response.getPagination( ).getNextPage( ) != null )
        {
            request.setPage( response.getPagination( ).getNextPage( ) );
            response = service.getSuspiciousIdentities( request, clientCode, this.getRequestAuthor( ) );
            suspiciousIdentities.addAll( response.getSuspiciousIdentities( ) );
        }
        assert suspiciousIdentities.size( ) == max;
    }

    @Test
    public void manageSuspiciousIdentities( ) throws IdentityStoreException
    {
        final SuspiciousIdentitySearchRequest request = new SuspiciousIdentitySearchRequest( );
        request.setRuleCode( duplicateRuleCode );

        final SuspiciousIdentitySearchResponse response = service.getSuspiciousIdentities( request, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( response.getStatus( ).getType( ), ResponseStatusType.OK ) && !response.getSuspiciousIdentities( ).isEmpty( );

        final SuspiciousIdentityDto suspiciousIdentityDto = response.getSuspiciousIdentities( ).get( 0 );
        final String customerId = suspiciousIdentityDto.getCustomerId( );
        final DuplicateSearchResponse duplicateSearchResponse = service.getDuplicates( customerId, duplicateRuleCode, clientCode, this.getRequestAuthor( ) );
        assert duplicateSearchResponse.getStatus( ).getType( ) == ResponseStatusType.OK && !duplicateSearchResponse.getIdentities( ).isEmpty( );

        final SuspiciousIdentityLockRequest lockRequest = new SuspiciousIdentityLockRequest( );
        lockRequest.setLocked( true );
        lockRequest.setCustomerId( customerId );
        final SuspiciousIdentityLockResponse lockResponse = service.lockIdentity( lockRequest, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( lockResponse.getStatus( ).getType( ), ResponseStatusType.SUCCESS ) && lockResponse.isLocked( );

        final SuspiciousIdentityExcludeRequest excludeRequest = new SuspiciousIdentityExcludeRequest( );
        excludeRequest.setIdentityCuid1( customerId );
        excludeRequest.setIdentityCuid2( duplicateSearchResponse.getIdentities( ).get( 0 ).getCustomerId( ) );
        final SuspiciousIdentityExcludeResponse excludeResponse = service.excludeIdentities( excludeRequest, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( excludeResponse.getStatus( ).getType( ), ResponseStatusType.SUCCESS );

        final SuspiciousIdentityExcludeResponse cancelResponse = service.cancelIdentitiesExclusion( excludeRequest, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( cancelResponse.getStatus( ).getType( ), ResponseStatusType.SUCCESS );

        final SuspiciousIdentityLockRequest unLockRequest = new SuspiciousIdentityLockRequest( );
        unLockRequest.setLocked( false );
        unLockRequest.setCustomerId( customerId );
        final SuspiciousIdentityLockResponse unLockResponse = service.lockIdentity( unLockRequest, clientCode, this.getRequestAuthor( ) );
        assert Objects.equals( unLockResponse.getStatus( ).getType( ), ResponseStatusType.SUCCESS ) && !unLockResponse.isLocked( );
    }
}
