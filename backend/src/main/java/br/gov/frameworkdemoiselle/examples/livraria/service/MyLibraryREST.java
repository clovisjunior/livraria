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

import java.io.Serializable;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import br.gov.frameworkdemoiselle.examples.livraria.business.MyLibraryBC;
import br.gov.frameworkdemoiselle.examples.livraria.entity.Book;
import br.gov.frameworkdemoiselle.examples.livraria.entity.MyLibrary;
import br.gov.frameworkdemoiselle.examples.livraria.security.Roles;
import br.gov.frameworkdemoiselle.security.LoggedIn;
import br.gov.frameworkdemoiselle.security.RequiredRole;
import br.gov.frameworkdemoiselle.transaction.Transactional;
import br.gov.frameworkdemoiselle.util.ValidatePayload;

@Api(value = "mylibrary")
@Path("mylibrary")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public class MyLibraryREST implements Serializable {
	
	private static final long serialVersionUID = -7174111271328015277L;
	
	@Inject
    private MyLibraryBC bc;

    @POST
    @Transactional
    @ValidatePayload
    @LoggedIn
    @RequiredRole({Roles.ADMINISTRADOR, Roles.USUARIO})
    @ApiOperation(value = "Adicionar um livro na biblioteca do usuário",
                  response = Book.class,
                  authorizations = {
                      @Authorization(value = "JWT",
                                     scopes = {
                                         @AuthorizationScope(scope = "read:events", description = "Read your events")
                                     })
                  }
    )
    public Response insert(final Book book) {
    	bc.addToMyLibrary(book);
        return Response.ok().build();
    }
    
    @GET
    @LoggedIn
    @RequiredRole({Roles.ADMINISTRADOR, Roles.USUARIO})
    @ApiOperation(value = "Retorna todos os livros na biblioteca do usuário",
                  response = MyLibrary.class,
                  responseContainer = "List",
                  authorizations = {
                      @Authorization(value = "JWT",
                                     scopes = {
                                         @AuthorizationScope(scope = "read:events", description = "Read your events")
                                     })
                  }
    )
    public Response listMyBooks() {
        return Response.ok().entity(bc.listMyBooks()).build();
    }

}
