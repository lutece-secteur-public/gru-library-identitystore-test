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

import fr.paris.lutece.plugins.identityimport.web.service.BatchImportService;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AttributeDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.BatchDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.IdentityDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchImportRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchImportResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchStatusMode;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchStatusRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.importing.BatchStatusResponse;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;

/**
 * test of NotificationService
 */

public class BatchImportServiceTest extends AbstractServiceTest
{
    @Resource( name = "identityService.import.httpAccess" )
    private BatchImportService service;

    @Test
    public void testImportBatches( ) throws IdentityStoreException
    {
        final BatchImportRequest request = new BatchImportRequest( );
        final BatchDto batch = new BatchDto( );
        request.setBatch( batch );
        batch.setComment( "Créé automatiquement par un test JUNIT" );
        batch.setUser( "junit@test.com" );

        final IdentityDto identityDto = new IdentityDto( );
        batch.getIdentities( ).add( identityDto );
        identityDto.setExternalCustomerId( "test-junit-1" );

        final AttributeDto gender = new AttributeDto( );
        identityDto.getAttributes( ).add( gender );
        gender.setKey( "gender" );
        gender.setValue( "0" );
        gender.setCertifier( certificationCode );
        gender.setCertificationDate( new Date( ) );

        final AttributeDto familyName = new AttributeDto( );
        identityDto.getAttributes( ).add( familyName );
        familyName.setKey( "family_name" );
        familyName.setValue( "Testun" );
        familyName.setCertifier( certificationCode );
        familyName.setCertificationDate( new Date( ) );

        final AttributeDto firstName = new AttributeDto( );
        identityDto.getAttributes( ).add( firstName );
        firstName.setKey( "first_name" );
        firstName.setValue( "Robert" );
        firstName.setCertifier( certificationCode );
        firstName.setCertificationDate( new Date( ) );

        final AttributeDto birthdate = new AttributeDto( );
        identityDto.getAttributes( ).add( birthdate );
        birthdate.setKey( "birthdate" );
        birthdate.setValue( "11/11/1986" );
        birthdate.setCertifier( certificationCode );
        birthdate.setCertificationDate( new Date( ) );

        final BatchImportResponse response = service.importBatch( request, clientCode, getRequestAuthor( ) );
        assert response != null && response.getStatus( ) != null && response.getStatus( ).getHttpCode( ) == 201 && response.getReference( ) != null;

        final BatchStatusRequest batchStatusRequest = new BatchStatusRequest( );
        batchStatusRequest.setBatchReference( response.getReference( ) );
        batchStatusRequest.setMode( BatchStatusMode.FULL );

        final BatchStatusResponse batchStatus = service.getBatchStatus( batchStatusRequest, clientCode, getRequestAuthor( ) );
        assert batchStatus != null && batchStatus.getStatus( ) != null && batchStatus.getStatus( ).getHttpCode( ) == 200;

    }

}
