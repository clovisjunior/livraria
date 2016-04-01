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
package br.gov.frameworkdemoiselle.examples.livraria.business;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.examples.livraria.entity.Book;
import br.gov.frameworkdemoiselle.examples.livraria.entity.MyLibrary;
import br.gov.frameworkdemoiselle.examples.livraria.entity.User;
import br.gov.frameworkdemoiselle.examples.livraria.persistence.MyLibraryDAO;
import br.gov.frameworkdemoiselle.security.SecurityContext;
import br.gov.frameworkdemoiselle.stereotype.BusinessController;
import br.gov.frameworkdemoiselle.template.DelegateCrud;

@BusinessController
public class MyLibraryBC extends DelegateCrud<MyLibrary, Long, MyLibraryDAO> {

	private static final long serialVersionUID = -8538777512511927764L;
	
	@Inject
	private SecurityContext securityContext;

	public void addToMyLibrary(Book book) {
		User user = (User) securityContext.getUser();
		
		MyLibrary myLibrary = new MyLibrary();
		myLibrary.setBook(book);
		myLibrary.setCreatedDate(new Date());
		myLibrary.setUser(user);
		
		getDelegate().insert(myLibrary);
	}

	public List<MyLibrary> listMyBooks() {
		User user = (User) securityContext.getUser();
		return getDelegate().listMyBooks(user);
	}

	public void removeBookFromLibraries(Long bookId) {
		getDelegate().removeBookFromLibraries(bookId);
	}
   

}
