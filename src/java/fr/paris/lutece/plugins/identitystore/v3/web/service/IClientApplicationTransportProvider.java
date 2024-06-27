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
package fr.paris.lutece.plugins.identitystore.v3.web.service;

import fr.paris.lutece.plugins.identitystore.v3.web.rs.IdentityRequestValidator;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.application.ClientApplicationDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.application.ClientChangeResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.application.ClientSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.application.ClientsSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.RequestAuthor;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.ServiceContractChangeResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.ServiceContractDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.ServiceContractSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.contract.ServiceContractsSearchResponse;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;

public interface IClientApplicationTransportProvider
{

    /**
     * Get all clients that belongs to given targetApplicationCode.
     *
     * @param targetApplicationCode
     *            the application code.
     * @param strClientCode
     *            code of the client that performs the request
     * @param author
     *            author of the request
     * @return ServiceContractsSearchResponse
     */
    ClientsSearchResponse getClients( final String targetApplicationCode, final String strClientCode, final RequestAuthor author )
            throws IdentityStoreException;

    /**
     * Get a client by its client code.
     *
     * @param targetClientCode
     *            the client code.
     * @param strClientCode
     *            code of the client that performs the request
     * @param author
     *            author of the request
     * @return ServiceContractsSearchResponse
     */
    ClientSearchResponse getClient( final String targetClientCode, final String strClientCode, final RequestAuthor author ) throws IdentityStoreException;

    /**
     * Create a new client.
     *
     * @param clientDto
     *            the DTO representing the client to create.
     * @param strClientCode
     *            code of the client that performs the request
     * @param author
     *            author of the request
     * @return ServiceContractsSearchResponse
     */
    ClientChangeResponse createClient( final ClientApplicationDto clientDto, final String strClientCode, final RequestAuthor author )
            throws IdentityStoreException;

    /**
     * Update an existing client.
     *
     * @param clientDto
     *            the DTO representing the client to update.
     * @param strClientCode
     *            code of the client that performs the request
     * @param author
     *            author of the request
     * @return ServiceContractsSearchResponse
     */
    ClientChangeResponse updateClient( final ClientApplicationDto clientDto, final String strClientCode, final RequestAuthor author )
            throws IdentityStoreException;

    default void checkCommonHeaders( final String strClientCode, final RequestAuthor author ) throws IdentityStoreException
    {
        IdentityRequestValidator.instance( ).checkAuthor( author );
        IdentityRequestValidator.instance( ).checkClientCode( strClientCode );
    }
}
