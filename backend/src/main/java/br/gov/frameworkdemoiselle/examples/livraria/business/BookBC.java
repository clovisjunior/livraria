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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import br.gov.frameworkdemoiselle.HttpViolationException;
import br.gov.frameworkdemoiselle.examples.livraria.entity.Book;
import br.gov.frameworkdemoiselle.examples.livraria.persistence.BookDAO;
import br.gov.frameworkdemoiselle.lifecycle.Startup;
import br.gov.frameworkdemoiselle.stereotype.BusinessController;
import br.gov.frameworkdemoiselle.template.DelegateCrud;
import br.gov.frameworkdemoiselle.transaction.Transactional;

@BusinessController
public class BookBC extends DelegateCrud<Book, Long, BookDAO> {

	private static final long serialVersionUID = -1426016403816422813L;
	
	@Inject
	private MyLibraryBC myLibraryBC;

	@Startup
    @Transactional
    public void load() {
        if (findAll().isEmpty()) {
        	
        	try {
        	
        		ClassLoader cl = getClass().getClassLoader();
        		File file = new File(cl.getResource("/files/demoiselle-framework-reference.pdf").getFile());
	        	InputStream is = new FileInputStream(file);
	        	byte[] bytes = new byte[(int) file.length()];
	        	is.read(bytes);
	        	is.close();
	        	
	        	Book book = new Book();
	        	book.setDescription("Documentação do Framework Demoiselle 2.5");
	        	book.setTitle("Framework Demoiselle");
				book.setFile(bytes);
				
				Book book2 = new Book();
				book2.setDescription("Documentação do Framework Demoiselle 2.4");
				book2.setTitle("Framework Demoiselle");
				book2.setFile(bytes);
				
				getDelegate().insert(book);
				getDelegate().insert(book2);
        	
        	} catch (IOException e) {
				e.printStackTrace();
			}
        	
        }
    }

    public Long count() {
        return getDelegate().count();
    }

    @SuppressWarnings("rawtypes")
	public List list(String field, String order, int init, int qtde) {
        return getDelegate().list(field, order, init, qtde);
    }

    @SuppressWarnings("rawtypes")
	public List list(String campo, String valor) {
        return getDelegate().list(campo, valor);
    }

	public void uploadFile(MultipartFormDataInput input, Long bookId) throws HttpViolationException{
		Book book = getDelegate().load(bookId);

        Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
        List<InputPart> inputPartsAnexo = uploadForm.get("file");

        for (InputPart inputPart : inputPartsAnexo) {
            try {
                InputStream inputStream = inputPart.getBody(InputStream.class, null);                
                byte[] bytes = IOUtils.toByteArray(inputStream);
	        	
                book.setFile(bytes);              
                getDelegate().update(book);
            } catch (Exception e) {
            	HttpViolationException httpException = new HttpViolationException(HttpStatus.SC_INTERNAL_SERVER_ERROR);
                httpException.addViolation("Não foi possível salvar o anexo");
                throw httpException;
            }
        }			
		
	}
	
	@Override
	public void delete(Long id) {
		myLibraryBC.removeBookFromLibraries(id);
		super.delete(id);
	}

	public void deleteAttachment(Long id) {
		Book book = getDelegate().load(id);
		book.setFile(null);
		getDelegate().update(book);
	}

}
