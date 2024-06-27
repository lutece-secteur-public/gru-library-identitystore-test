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
package fr.paris.lutece.plugins.identitystore.v3.web.rs.service;

import fr.paris.lutece.plugins.identitystore.v3.web.rs.IdentityRequestValidator;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.application.ClientApplicationDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.application.ClientChangeResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.application.ClientSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.application.ClientsSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.RequestAuthor;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.util.Constants;
import fr.paris.lutece.plugins.identitystore.v3.web.service.IClientApplicationTransportProvider;
import fr.paris.lutece.plugins.identitystore.v3.web.service.IHttpTransportProvider;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;

import java.util.HashMap;
import java.util.Map;

public class ClientApplicationTransportRest extends AbstractTransportRest implements IClientApplicationTransportProvider
{
    /** URL for identityStore REST service */
    private final String _strIdentityStoreEndPoint;
    private final String _strIdentityPath;

    /**
     * Constructor with IHttpTransportProvider parameter
     *
     * @param httpTransport
     *            the provider to use
     */
    public ClientApplicationTransportRest( final IHttpTransportProvider httpTransport )
    {
        super( httpTransport );

        _strIdentityStoreEndPoint = httpTransport.getApiEndPointUrl( );
        _strIdentityPath = Constants.CONSTANT_DEFAULT_IDENTITY_PATH;
    }

    public ClientApplicationTransportRest( final IHttpTransportProvider httpTransport, final String strIdentityPath )
    {
        super( httpTransport );

        _strIdentityStoreEndPoint = httpTransport.getApiEndPointUrl( );
        _strIdentityPath = strIdentityPath;
    }

    @Override
    public ClientsSearchResponse getClients( String targetApplicationCode, String strClientCode, RequestAuthor author ) throws IdentityStoreException
    {
        IdentityRequestValidator.instance( ).checkApplicationCode( targetApplicationCode );
        this.checkCommonHeaders( strClientCode, author );

        final Map<String, String> mapHeadersRequest = new HashMap<>( );
        mapHeadersRequest.put( Constants.PARAM_CLIENT_CODE, strClientCode );
        mapHeadersRequest.put( Constants.PARAM_AUTHOR_NAME, author.getName( ) );
        mapHeadersRequest.put( Constants.PARAM_AUTHOR_TYPE, author.getType( ).name( ) );

        final String url = _strIdentityStoreEndPoint + _strIdentityPath + Constants.VERSION_PATH_V3 + Constants.CLIENTS_PATH + "/" + targetApplicationCode;

        return _httpTransport.doGet( url, null, mapHeadersRequest, ClientsSearchResponse.class, _mapper );
    }

    @Override
    public ClientSearchResponse getClient( String targetClientCode, String strClientCode, RequestAuthor author ) throws IdentityStoreException
    {
        IdentityRequestValidator.instance( ).checkClientCode( targetClientCode );
        this.checkCommonHeaders( strClientCode, author );

        final Map<String, String> mapHeadersRequest = new HashMap<>( );
        mapHeadersRequest.put( Constants.PARAM_CLIENT_CODE, strClientCode );
        mapHeadersRequest.put( Constants.PARAM_AUTHOR_NAME, author.getName( ) );
        mapHeadersRequest.put( Constants.PARAM_AUTHOR_TYPE, author.getType( ).name( ) );

        final String url = _strIdentityStoreEndPoint + _strIdentityPath + Constants.VERSION_PATH_V3 + Constants.CLIENT_PATH + "/" + targetClientCode;
        return _httpTransport.doGet( url, null, mapHeadersRequest, ClientSearchResponse.class, _mapper );
    }

    @Override
    public ClientChangeResponse createClient( ClientApplicationDto clientDto, String strClientCode, RequestAuthor author ) throws IdentityStoreException
    {
        IdentityRequestValidator.instance( ).checkClientApplicationDto( clientDto );
        this.checkCommonHeaders( strClientCode, author );

        final Map<String, String> mapHeadersRequest = new HashMap<>( );
        mapHeadersRequest.put( Constants.PARAM_CLIENT_CODE, strClientCode );
        mapHeadersRequest.put( Constants.PARAM_AUTHOR_NAME, author.getName( ) );
        mapHeadersRequest.put( Constants.PARAM_AUTHOR_TYPE, author.getType( ).name( ) );

        final String url = _strIdentityStoreEndPoint + _strIdentityPath + Constants.VERSION_PATH_V3 + Constants.CLIENT_PATH;
        return _httpTransport.doPostJSON( url, null, mapHeadersRequest, clientDto, ClientChangeResponse.class, _mapper );
    }

    @Override
    public ClientChangeResponse updateClient( ClientApplicationDto clientDto, String strClientCode, RequestAuthor author ) throws IdentityStoreException
    {
        IdentityRequestValidator.instance( ).checkClientApplicationDto( clientDto );
        this.checkCommonHeaders( strClientCode, author );

        final Map<String, String> mapHeadersRequest = new HashMap<>( );
        mapHeadersRequest.put( Constants.PARAM_CLIENT_CODE, strClientCode );
        mapHeadersRequest.put( Constants.PARAM_AUTHOR_NAME, author.getName( ) );
        mapHeadersRequest.put( Constants.PARAM_AUTHOR_TYPE, author.getType( ).name( ) );

        final String url = _strIdentityStoreEndPoint + _strIdentityPath + Constants.VERSION_PATH_V3 + Constants.CLIENT_PATH + "/" + clientDto.getClientCode();
        return _httpTransport.doPutJSON( url, null, mapHeadersRequest, clientDto, ClientChangeResponse.class, _mapper );
    }
}
