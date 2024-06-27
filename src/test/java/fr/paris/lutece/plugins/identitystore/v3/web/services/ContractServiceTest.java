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
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.ResponseStatusType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.AttributeDefinitionDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.AttributeRequirementDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.AttributeRightDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.CertificationProcessusDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.ServiceContractChangeResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.ServiceContractDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.ServiceContractSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.ServiceContractsSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.service.ClientApplicationService;
import fr.paris.lutece.plugins.identitystore.v3.web.service.ServiceContractService;
import fr.paris.lutece.plugins.identitystore.v3.web.service.ServiceContractServiceExtended;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import org.junit.Test;

import javax.annotation.Resource;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * test of NotificationService
 */

public class ContractServiceTest extends AbstractServiceTest
{
    @Resource( name = "serviceContract.rest.httpAccess" )
    private ServiceContractService service;

    @Resource( name = "serviceContractExtended.rest.httpAccess" )
    private ServiceContractServiceExtended extendedService;

    @Resource( name = "identityService.clients.httpAccess" )
    private ClientApplicationService clientApplicationService;

    @Test
    public void testGetActiveServiceContract( ) throws IdentityStoreException
    {
        final ServiceContractSearchResponse response = service.getActiveServiceContract( clientCode, clientCode, getRequestAuthor( ) );
        assert Objects.equals( response.getStatus( ).getType( ), ResponseStatusType.OK );
    }

    @Test
    public void testGetServiceContractForClient( ) throws IdentityStoreException
    {
        final ServiceContractsSearchResponse serviceContractList = extendedService.getServiceContractList( clientCode, clientCode, getRequestAuthor( ) );
        assert Objects.equals( serviceContractList.getStatus( ).getType( ), ResponseStatusType.OK );
        assert !serviceContractList.getServiceContracts( ).isEmpty( );

        for ( final ServiceContractDto serviceContract : serviceContractList.getServiceContracts( ) )
        {
            final ServiceContractSearchResponse response = extendedService.getServiceContract( serviceContract.getId( ), clientCode, getRequestAuthor( ) );
            assert Objects.equals( response.getStatus( ).getType( ), ResponseStatusType.OK );
        }
    }

    @Test
    public void testGetServiceContractForAllClients( ) throws IdentityStoreException
    {
        final ServiceContractsSearchResponse serviceContractList = extendedService.getAllServiceContractList( clientCode, getRequestAuthor( ) );
        assert Objects.equals( serviceContractList.getStatus( ).getType( ), ResponseStatusType.OK );
        assert !serviceContractList.getServiceContracts( ).isEmpty( );
    }

    @Test
    public void manageServiceContract( ) throws IdentityStoreException
    {
        final String strTargetClientCode = clientCode + "-" + UUID.randomUUID( );

        final ClientApplicationDto clientDto = new ClientApplicationDto( );
        clientDto.setClientCode( strTargetClientCode );
        clientDto.setName( strTargetClientCode );
        clientDto.setApplicationCode( serviceContractsApplicationCode );
        final ClientChangeResponse clientChangeResponse = clientApplicationService.createClient( clientDto, clientCode, getRequestAuthor( ) );
        assert Objects.equals( clientChangeResponse.getStatus( ).getType( ), ResponseStatusType.SUCCESS );

        final ServiceContractDto testServiceContract = this.buildServiceContract( strTargetClientCode, "Test Service contract", "2022-01-01", null,
                Constants.PARAM_FIRST_NAME, Constants.PARAM_FAMILY_NAME, Constants.PARAM_BIRTH_DATE );

        final ServiceContractChangeResponse createResponse = extendedService.createServiceContract( testServiceContract, clientCode, getRequestAuthor( ) );
        assert Objects.equals( createResponse.getStatus( ).getType( ), ResponseStatusType.SUCCESS );

        final ServiceContractDto createdServiceContract = createResponse.getServiceContract( );
        createdServiceContract.setAuthorizedAccountUpdate( true );
        final ServiceContractChangeResponse updateResponse = extendedService.updateServiceContract( createdServiceContract, createdServiceContract.getId( ),
                clientCode, getRequestAuthor( ) );
        assert Objects.equals( updateResponse.getStatus( ).getType( ), ResponseStatusType.SUCCESS );

        final ServiceContractDto updatedServiceContract = updateResponse.getServiceContract( );
        updatedServiceContract.setEndingDate( Date.valueOf( "2024-01-01" ) );
        final ServiceContractChangeResponse closeReponse = extendedService.closeServiceContract( updatedServiceContract, updatedServiceContract.getId( ),
                clientCode, getRequestAuthor( ) );
        assert Objects.equals( closeReponse.getStatus( ).getType( ), ResponseStatusType.SUCCESS );
    }

    private ServiceContractDto buildServiceContract( final String clientCode, final String name, final String startDate, final String endDate,
            final String... keys )
    {
        final ServiceContractDto serviceContract = new ServiceContractDto( );
        serviceContract.setClientCode( clientCode );
        serviceContract.setName( name );
        serviceContract.setServiceType( "TEST_TYPE" );
        serviceContract.setAuthorizedCreation( true );
        serviceContract.setAuthorizedUpdate( true );
        serviceContract.setAuthorizedAccountUpdate( false );
        serviceContract.setAuthorizedDeletion( true );
        serviceContract.setAuthorizedExport( true );
        serviceContract.setAuthorizedMerge( true );
        serviceContract.setAuthorizedImport( true );
        serviceContract.setAuthorizedSearch( true );
        serviceContract.setAuthorizedDecertification( true );
        serviceContract.setAuthorizedAgentHistoryRead( true );
        serviceContract.setDataRetentionPeriodInMonths( 24 );
        serviceContract.setMoaContactName( "moa_contact" );
        serviceContract.setMoaEntityName( "MOA" );
        serviceContract.setMoeResponsibleName( "moe_responsible" );
        serviceContract.setMoeEntityName( "MOE" );
        serviceContract.setStartingDate( Date.valueOf( startDate ) );
        if ( !Objects.isNull( endDate ) )
        {
            serviceContract.setEndingDate( Date.valueOf( endDate ) );
        }

        final List<AttributeDefinitionDto> attributeDefinitions = Arrays.stream( keys ).map( key -> {
            final AttributeDefinitionDto attributeDefinitionDto = new AttributeDefinitionDto( );
            attributeDefinitionDto.setCertifiable( true );
            attributeDefinitionDto.setName( key );
            attributeDefinitionDto.setKeyName( key );
            final AttributeRequirementDto attributeRequirement = new AttributeRequirementDto( );
            attributeRequirement.setLevel( certificationLevel );
            attributeDefinitionDto.setAttributeRequirement( attributeRequirement );
            final AttributeRightDto attributeRight = new AttributeRightDto( );
            attributeRight.setMandatory( true );
            attributeRight.setReadable( true );
            attributeRight.setSearchable( true );
            attributeRight.setWritable( true );
            attributeDefinitionDto.setAttributeRight( attributeRight );
            final List<CertificationProcessusDto> attributeCertifications = new ArrayList<>( );
            final CertificationProcessusDto certificationProcessus = new CertificationProcessusDto( );
            certificationProcessus.setCode( certificationCode );
            attributeCertifications.add( certificationProcessus );
            attributeDefinitionDto.setAttributeCertifications( attributeCertifications );
            return attributeDefinitionDto;
        } ).collect( Collectors.toList( ) );
        serviceContract.setAttributeDefinitions( attributeDefinitions );

        return serviceContract;
    }
}
