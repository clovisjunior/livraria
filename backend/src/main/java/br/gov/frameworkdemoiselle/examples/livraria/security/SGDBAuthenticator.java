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
package br.gov.frameworkdemoiselle.examples.livraria.security;

import java.security.Principal;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import br.gov.frameworkdemoiselle.examples.livraria.business.UserBC;
import br.gov.frameworkdemoiselle.examples.livraria.cover.UserCover;
import br.gov.frameworkdemoiselle.examples.livraria.entity.User;
import br.gov.frameworkdemoiselle.security.AuthenticationException;
import br.gov.frameworkdemoiselle.security.Authenticator;
import br.gov.frameworkdemoiselle.security.Credentials;
import br.gov.frameworkdemoiselle.security.Token;
import br.gov.frameworkdemoiselle.util.Beans;

@RequestScoped
public class SGDBAuthenticator implements Authenticator {

	private static final long serialVersionUID = 1968658485338109606L;

	@Inject
    private Credentials credentials;

    private User user;

    @Inject
    private UserBC usuarioBC;

    @Inject
    private JWTManager jwt;

    @Inject
    private transient HttpServletRequest httpRequest;

    @Inject
    private Logger logger;

    @Override
    public void authenticate() {

        Token token = Beans.getReference(Token.class);

        UserCover cover = jwt.hasToken(token.getValue());

        if (cover == null) {

            if (credentials.getUsername() != null && credentials.getPassword() != null) {

                try {

                    this.user = usuarioBC.loadEmailPass(credentials.getUsername(), credentials.getPassword());

                    if (this.user != null) {
                        this.user.setIp(httpRequest.getRemoteAddr());
                        generateToken(token);
                    } else {
                        throw new AuthenticationException("Usuário ou senha inválidos");
                    }

                } catch (Exception ex) {
                    throw new AuthenticationException("Usuário ou senha inválidos");
                }

            }

        } else {
            this.user = new User();
            this.user.setId(cover.getId());
            this.user.setName(cover.getNome());
            this.user.setRole(cover.getPerfil());
        }

    }

    @Override
    public void unauthenticate() throws Exception {
        this.user = null;
    }

    @Override
    public Principal getUser() {
        return this.user;
    }

    private void generateToken(Token token) {
        String chave = jwt.addToken(this.user);
        token.setValue(chave);
        this.user.setToken(chave);
    }

}
