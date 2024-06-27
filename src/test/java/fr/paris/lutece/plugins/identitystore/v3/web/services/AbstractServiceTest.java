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

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.AuthorType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.common.RequestAuthor;
import fr.paris.lutece.util.httpaccess.HttpAccessService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = {
        "classpath:library-identitystore-test_context.xml"
} )
@PropertySource( "classpath:library-identitystore-test.properties" )
public abstract class AbstractServiceTest
{
    @Value( "${client.code}" )
    protected String clientCode;

    @Value( "${identity.cuid}" )
    protected String cuid;

    @Value( "${identity.guid}" )
    protected String guid;

    @Value( "${duplicate.rule.code}" )
    protected String duplicateRuleCode;

    @Value( "${author.name}" )
    protected String authorName;

    @Value( "${author.type}" )
    protected String authorType;

    @Value( "${certification.level}" )
    protected String certificationLevel;

    @Value( "${certification.code}" )
    protected String certificationCode;

    @Value( "${service.contracts.application.code}" )
    protected String serviceContractsApplicationCode;

    @Value( "${application.code}" )
    protected String applicationCode;
    protected final ObjectMapper mapper = new ObjectMapper( );

    public AbstractServiceTest( )
    {
        mapper.disable( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES );
        mapper.enable( SerializationFeature.WRAP_ROOT_VALUE );

        // Init HttpAccess singleton through NPE exception due of lack of properties access
        try
        {
            HttpAccessService.getInstance( );
        }
        catch( Exception e )
        {
            // do nothing
        }
    }

    protected RequestAuthor getRequestAuthor( )
    {
        final RequestAuthor author = new RequestAuthor( );
        author.setName( authorName );
        author.setType( AuthorType.valueOf( authorType ) );
        return author;
    }
}
