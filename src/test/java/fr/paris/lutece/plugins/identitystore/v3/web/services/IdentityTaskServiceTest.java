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

import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.task.IdentityResourceType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.task.IdentityTaskCreateRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.task.IdentityTaskCreateResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.task.IdentityTaskDto;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.task.IdentityTaskGetResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.task.IdentityTaskGetStatusResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.task.IdentityTaskSearchRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.task.IdentityTaskSearchResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.task.IdentityTaskStatusType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.task.IdentityTaskType;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.task.IdentityTaskUpdateStatusRequest;
import fr.paris.lutece.plugins.identitystore.v3.web.rs.dto.task.IdentityTaskUpdateStatusResponse;
import fr.paris.lutece.plugins.identitystore.v3.web.service.IdentityService;
import fr.paris.lutece.plugins.identitystore.v3.web.service.IdentityServiceExtended;
import fr.paris.lutece.plugins.identitystore.web.exception.IdentityStoreException;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Objects;

/**
 * test of NotificationService
 */
public class IdentityTaskServiceTest extends AbstractServiceTest
{
    @Resource( name = "identityService.rest.httpAccess" )
    private IdentityService service;

    @Resource( name = "identityServiceExtended.rest.httpAccess" )
    private IdentityServiceExtended extendedService;

    @Test
    public void test( ) throws IdentityStoreException
    {
        final IdentityTaskCreateRequest createRequest = new IdentityTaskCreateRequest();
        createRequest.setTask(new IdentityTaskDto());
        createRequest.getTask().setTaskType(IdentityTaskType.EMAIL_VALIDATION_REQUEST.name());
        createRequest.getTask().setResourceId("21f4662f-5174-48d4-806d-2d877e19dd32");
        createRequest.getTask().setResourceType(IdentityResourceType.CUID.name());
        final IdentityTaskCreateResponse createTaskResponse = service.createIdentityTask( createRequest, clientCode, this.getRequestAuthor( ) );
        assert createTaskResponse.getStatus( ).getHttpCode( ) == 201;

        final IdentityTaskGetStatusResponse identityTaskStatus = service.getIdentityTaskStatus(createTaskResponse.getTaskCode(), clientCode, this.getRequestAuthor());
        assert identityTaskStatus.getStatus().getHttpCode() == 200
                && Objects.equals(identityTaskStatus.getTaskStatus(), IdentityTaskStatusType.TODO.name());

        final IdentityTaskGetResponse identityTask = extendedService.getIdentityTask(createTaskResponse.getTaskCode(), clientCode, this.getRequestAuthor());
        assert identityTask.getStatus().getHttpCode() == 200
                && Objects.equals(identityTask.getTask().getTaskType(), IdentityTaskType.EMAIL_VALIDATION_REQUEST.name())
                && Objects.equals(identityTask.getTask().getTaskStatus(), IdentityTaskStatusType.TODO)
                && Objects.equals(identityTask.getTask().getTaskCode(), createTaskResponse.getTaskCode());

        final IdentityTaskUpdateStatusRequest updateStatusRequest = new IdentityTaskUpdateStatusRequest();
        updateStatusRequest.setStatus(IdentityTaskStatusType.IN_PROGRESS);
        final IdentityTaskUpdateStatusResponse identityTaskUpdateStatusResponse = extendedService.updateIdentityTaskStatus(createTaskResponse.getTaskCode(), updateStatusRequest, clientCode, this.getRequestAuthor());
        assert identityTaskUpdateStatusResponse.getStatus().getHttpCode() == 200;

        final IdentityTaskGetResponse updatedTask = extendedService.getIdentityTask(createTaskResponse.getTaskCode(), clientCode, this.getRequestAuthor());
        assert updatedTask.getStatus().getHttpCode() == 200
                && Objects.equals(updatedTask.getTask().getTaskType(), IdentityTaskType.EMAIL_VALIDATION_REQUEST.name())
                && Objects.equals(updatedTask.getTask().getTaskStatus(), IdentityTaskStatusType.IN_PROGRESS)
                && Objects.equals(updatedTask.getTask().getTaskCode(), createTaskResponse.getTaskCode());

        final IdentityTaskSearchRequest request = new IdentityTaskSearchRequest();
        request.setTaskStatus(Arrays.asList(IdentityTaskStatusType.IN_PROGRESS, IdentityTaskStatusType.TODO));
        request.setTaskType(IdentityTaskType.EMAIL_VALIDATION_REQUEST);
        final IdentityTaskSearchResponse identityTaskSearchResponse = extendedService.searchIdentityTasks(request, clientCode, this.getRequestAuthor());
        assert identityTaskSearchResponse.getStatus().getHttpCode() == 200
                && !identityTaskSearchResponse.getTasks().isEmpty();
    }
}
