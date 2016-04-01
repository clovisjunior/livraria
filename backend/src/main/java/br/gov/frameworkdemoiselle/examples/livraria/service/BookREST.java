/*
 * Demoiselle Framework
 * Copyright (C) 2016 SERPRO
 * ----------------------------------------------------------------------------
 * This file is part of Demoiselle Framework.
 * 
 * Demoiselle Framework is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License version 3
 * as published by the Free Software Foundation.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License version 3
 * along with this program; if not,  see <http://www.gnu.org/licenses/>
 * or write to the Free Software Foundation, Inc., 51 Franklin Street,
 * Fifth Floor, Boston, MA  02110-1301, USA.
 * ----------------------------------------------------------------------------
 * Este arquivo é parte do Framework Demoiselle.
 * 
 * O Framework Demoiselle é um software livre; você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da GNU LGPL versão 3 como publicada pela Fundação
 * do Software Livre (FSF).
 * 
 * Este programa é distribuído na esperança que possa ser útil, mas SEM NENHUMA
 * GARANTIA; sem uma garantia implícita de ADEQUAÇÃO a qualquer MERCADO ou
 * APLICAÇÃO EM PARTICULAR. Veja a Licença Pública Geral GNU/LGPL em português
 * para maiores detalhes.
 * 
 * Você deve ter recebido uma cópia da GNU LGPL versão 3, sob o título
 * "LICENCA.txt", junto com esse programa. Se não, acesse <http://www.gnu.org/licenses/>
 * ou escreva para a Fundação do Software Livre (FSF) Inc.,
 * 51 Franklin St, Fifth Floor, Boston, MA 02111-1301, USA.
 */
package br.gov.frameworkdemoiselle.examples.livraria.service;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.List;
import java.util.ListIterator;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.MULTIPART_FORM_DATA;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import br.gov.frameworkdemoiselle.HttpViolationException;
import br.gov.frameworkdemoiselle.NotFoundException;
import br.gov.frameworkdemoiselle.examples.livraria.business.BookBC;
import br.gov.frameworkdemoiselle.examples.livraria.entity.Book;
import br.gov.frameworkdemoiselle.examples.livraria.security.Roles;
import br.gov.frameworkdemoiselle.examples.livraria.util.Util;
import br.gov.frameworkdemoiselle.security.LoggedIn;
import br.gov.frameworkdemoiselle.security.RequiredRole;
import br.gov.frameworkdemoiselle.transaction.Transactional;
import br.gov.frameworkdemoiselle.util.ValidatePayload;

@Api(value = "book")
@Path("book")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class BookREST implements Serializable {

	private static final long serialVersionUID = -1252454308789946485L;
	
	@Inject
    private BookBC bc;

    @GET
    @Path("list/{field}/{order}/{init}/{qtde}")
    @Transactional
    @LoggedIn
    @RequiredRole({Roles.ADMINISTRADOR})
    @ApiOperation(value = "Lista com paginação no servidor",
                  notes = "Informe o campo/ordem(asc/desc)/posição do primeiro registro/quantidade de registros",
                  response = Book.class
    )
    public Response list(@PathParam("field") String field, @PathParam("order") String order, @PathParam("init") int init, @PathParam("qtde") int qtde) throws NotFoundException {
        if ((order.equalsIgnoreCase("asc") || order.equalsIgnoreCase("desc")) && (Util.fieldInClass(field, Book.class))) {
            return Response.ok().entity(bc.list(field, order, init, qtde)).build();
        }
        return Response.ok().entity(null).build();
    }

    @GET
    @Path("{field}/{value}")
    @Transactional
    @LoggedIn
    @RequiredRole({Roles.ADMINISTRADOR})
    @ApiOperation(value = "Lista com onde é informado o campo e valor",
                  notes = "Informe o campo/valor do campo",
                  response = Book.class
    )
    public Response list(@PathParam("field") final String campo, @PathParam("value") final String valor) {
        if ((Util.fieldInClass(campo, Book.class))) {
            return Response.ok().entity(bc.list(campo, valor)).build();
        }
        return Response.ok().entity(null).build();
    }

    @GET
    @Path("count")
    @Transactional
    @LoggedIn
    @RequiredRole({Roles.ADMINISTRADOR, Roles.USUARIO})
    @ApiOperation(value = "Quantidade de registro",
                  notes = "Usado para trabalhar as tabelas com paginação no servidor",
                  response = Integer.class
    )
    public Response count() throws NotFoundException {
        return Response.ok().entity(bc.count()).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    @LoggedIn
    @RequiredRole({Roles.ADMINISTRADOR})
    @ApiOperation(value = "Remove entidade",
                  response = Book.class,
                  authorizations = {
                      @Authorization(value = "JWT",
                                     scopes = {
                                         @AuthorizationScope(scope = "read:events", description = "Read your events")
                                     })
                  }
    )
    public void delete(@PathParam("id") final Long id) {
        bc.delete(id);
    }

    @DELETE
    @Path("{ids}")
    @Transactional
    @LoggedIn
    @RequiredRole({Roles.ADMINISTRADOR})
    @ApiOperation(value = "Remove várias entidades a partir de um lista de IDs",
                  response = Book.class,
                  authorizations = {
                      @Authorization(value = "JWT",
                                     scopes = {
                                         @AuthorizationScope(scope = "read:events", description = "Read your events")
                                     })
                  }
    )
    public void delete(@PathParam("ids") final List<Long> ids) {
        ListIterator<Long> iter = ids.listIterator();

        while (iter.hasNext()) {
            this.delete(iter.next());
        }
    }

    @GET
    @ApiOperation(value = "Lista de todos os registros", response = Book.class)
    @LoggedIn
    @RequiredRole({Roles.ADMINISTRADOR, Roles.USUARIO})
    public Response findAll() {
        return Response.ok().entity(bc.findAll()).build();
    }

    @POST
    @Transactional
    @ValidatePayload
    @LoggedIn
    @RequiredRole({Roles.ADMINISTRADOR})
    @ApiOperation(value = "Insere entidade no banco",
                  response = Book.class,
                  authorizations = {
                      @Authorization(value = "JWT",
                                     scopes = {
                                         @AuthorizationScope(scope = "read:events", description = "Read your events")
                                     })
                  }
    )
    public Response insert(final Book bean) {
        return Response.ok().entity(bc.insert(bean)).build();
    }

    @GET
    @Path("{id}")
    @Transactional
    @LoggedIn
    @RequiredRole({Roles.ADMINISTRADOR, Roles.USUARIO})
    @ApiOperation(value = "Busca entidade a partir do ID",
                  response = Book.class,
                  authorizations = {
                      @Authorization(value = "JWT",
                                     scopes = {
                                         @AuthorizationScope(scope = "read:events", description = "Read your events")
                                     })
                  }
    )
    public Response load(@PathParam("id") final Long id) {
        return Response.ok().entity(bc.load(id)).build();
    }

    @PUT
    @Transactional
    @ValidatePayload
    @LoggedIn
    @RequiredRole({Roles.ADMINISTRADOR})
    @ApiOperation(value = "Atualiza a entidade",
                  response = Book.class,
                  authorizations = {
                      @Authorization(value = "JWT",
                                     scopes = {
                                         @AuthorizationScope(scope = "read:events", description = "Read your events")
                                     })
                  }
    )
    public Response update(final Book bean) {
        return Response.ok().entity(bc.update(bean)).build();
    }
    
    @POST
    @Transactional
    @LoggedIn
    @Path("upload/{id}")
    @Consumes(MULTIPART_FORM_DATA)
	    @ApiOperation(value = "Realiza o upload do anexo do livro",	    
	    authorizations = {
	        @Authorization(value = "JWT",
	                       scopes = {
	                           @AuthorizationScope(scope = "read:events", description = "Read your events")
	                       })
	    }
	)
    public Response upload(MultipartFormDataInput input, @PathParam("id") Long id) {
    	try{
			bc.uploadFile(input, id);
	        return Response.ok().build();    
	    } catch (HttpViolationException ex) {
	        return Response.status(ex.getStatusCode()).entity(ex.getViolations()).build();
	    }
    }
    
    @GET    
    @Path("download/{id}")
    @Produces("application/force-download")
    @ApiOperation(value = "Realiza o download do anexo do livro",	    
	    authorizations = {
	        @Authorization(value = "JWT",
	                       scopes = {
	                           @AuthorizationScope(scope = "read:events", description = "Read your events")
	                       })
	    }
	)
    public Response download(@PathParam("id") Long id) {
    	Book book = bc.load(id);
    	if(book.getFile() != null){
	    	final ByteArrayInputStream in = new ByteArrayInputStream(book.getFile());
	        return Response.ok(in, MediaType.APPLICATION_OCTET_STREAM)
	            .header("content-disposition", "attachment; filename = '" + book.getTitle() + ".pdf'").build();
    	}
    	else{
    		return Response.noContent().build();
    	}
    }
    
    @DELETE
    @Transactional
    @LoggedIn
    @Path("download/{id}")  
    @RequiredRole({Roles.ADMINISTRADOR})
    @ApiOperation(value = "Remove o anexo do livro",
	    response = Book.class,
	    authorizations = {
	        @Authorization(value = "JWT",
	                       scopes = {
	                           @AuthorizationScope(scope = "read:events", description = "Read your events")
	                       })
	    }
	)
    public Response deleteAttachment(@PathParam("id") Long id) {
    	bc.deleteAttachment(id);
    	return Response.ok().build();    
    }


}
