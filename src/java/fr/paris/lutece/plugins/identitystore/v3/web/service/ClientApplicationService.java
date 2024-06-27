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

import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.application.ClientApplicationDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.application.ClientChangeResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.application.ClientSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.application.ClientsSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.RequestAuthor;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;

/**
 * Service regarding service contracts.
 */
public class ClientApplicationService
{

    /** transport provider */
    private IClientApplicationTransportProvider _transportProvider;

    /**
     * Simple constructor.
     */
    public ClientApplicationService( )
    {
        super( );
    }

    /**
     * Constructor with transport provider in parameters
     *
     * @param transportProvider
     *            IServiceContractTransportProvider
     */
    public ClientApplicationService( final IClientApplicationTransportProvider transportProvider )
    {
        super( );
        this._transportProvider = transportProvider;
    }

    /**
     * setter of transportProvider parameter
     *
     * @param transportProvider
     *            IServiceContractTransportProvider
     */
    public void setTransportProvider( final IClientApplicationTransportProvider transportProvider )
    {
        this._transportProvider = transportProvider;
    }

    /**
     * Get all clients that belongs to given strTargetApplicationCode.
     *
     * @param strTargetApplicationCode
     *            the application code.
     * @param strClientCode
     *            code of the client that performs the request
     * @param author
     *            author of the request
     */
    public ClientsSearchResponse getClients( final String strTargetApplicationCode, final String strClientCode, final RequestAuthor author )
            throws IdentityStoreException
    {
        return this._transportProvider.getClients( strTargetApplicationCode, strClientCode, author );
    }

    /**
     * Get a client by its client code.
     *
     * @param strTargetClientCode
     *            the client code.
     * @param strClientCode
     *            code of the client that performs the request
     * @param author
     *            author of the request
     * @return ServiceContractsSearchResponse
     */
    public ClientSearchResponse getClient( final String strTargetClientCode, final String strClientCode, final RequestAuthor author )
            throws IdentityStoreException
    {
        return this._transportProvider.getClient( strTargetClientCode, strClientCode, author );
    }

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
    public ClientChangeResponse createClient( final ClientApplicationDto clientDto, final String strClientCode, final RequestAuthor author )
            throws IdentityStoreException
    {
        return this._transportProvider.createClient( clientDto, strClientCode, author );
    }

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
    public ClientChangeResponse updateClient( final ClientApplicationDto clientDto, final String strClientCode, final RequestAuthor author )
            throws IdentityStoreException
    {
        return this._transportProvider.updateClient( clientDto, strClientCode, author );
    }

}
