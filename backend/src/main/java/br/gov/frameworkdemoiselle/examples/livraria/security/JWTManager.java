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

import br.gov.frameworkdemoiselle.examples.livraria.AppConfig;
import br.gov.frameworkdemoiselle.examples.livraria.cover.UserCover;
import br.gov.frameworkdemoiselle.examples.livraria.entity.User;
import br.gov.frameworkdemoiselle.util.Beans;
import com.google.gson.Gson;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.RequestScoped;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jwk.RsaJwkGenerator;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;

@RequestScoped
public class JWTManager implements Serializable {

	private static final long serialVersionUID = 1742551428048550765L;

	private final AppConfig appConfig = Beans.getReference(AppConfig.class);

    private final RsaJsonWebKey rsaJsonWebKey;

    public JWTManager() throws JoseException {
        if (appConfig.getChave() == null) {
            RsaJsonWebKey chave = RsaJwkGenerator.generateJwk(2048);
            Logger.getLogger(JWTManager.class.getName()).log(Level.WARNING, "Coloque os parametros no app.properties e reinicie a app ");
            Logger.getLogger(JWTManager.class.getName()).log(Level.INFO, "jwt.key=" + chave.toJson(JsonWebKey.OutputControlLevel.INCLUDE_PRIVATE));
            rsaJsonWebKey = null;
        } else {
            rsaJsonWebKey = (RsaJsonWebKey) RsaJsonWebKey.Factory.newPublicJwk(appConfig.getChave());
        }
        rsaJsonWebKey.setKeyId("DEMOISELLE");
    }

    public String addToken(User user) {
        try {
            JwtClaims claims = new JwtClaims();
            claims.setIssuer(appConfig.getRemetente());
            claims.setAudience(appConfig.getDestinatario());
            claims.setExpirationTimeMinutesInTheFuture(appConfig.getTempo() == null ? 720 : appConfig.getTempo());
            claims.setGeneratedJwtId();
            claims.setIssuedAtToNow();
            claims.setNotBeforeMinutesInThePast(1);

            claims.setClaim("user", new Gson().toJson(new UserCover(user)));

            JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(claims.toJson());
            jws.setKey(rsaJsonWebKey.getPrivateKey());
            jws.setKeyIdHeaderValue(rsaJsonWebKey.getKeyId());
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
            return jws.getCompactSerialization();
        } catch (JoseException ex) {
            Logger.getLogger(JWTManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public UserCover hasToken(String jwt) {
        UserCover usuario = null;
        if (jwt != null && !jwt.isEmpty()) {
            JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime() // the JWT must have an expiration time
                .setAllowedClockSkewInSeconds(60) // allow some leeway in validating time based claims to account for clock skew
                .setExpectedIssuer(appConfig.getRemetente()) // whom the JWT needs to have been issued by
                .setExpectedAudience(appConfig.getDestinatario()) // to whom the JWT is intended for
                .setVerificationKey(rsaJsonWebKey.getKey()) // verify the signature with the public key
                .build(); // create the JwtConsumer instance

            try {
                JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
                
                usuario = new Gson().fromJson((String) jwtClaims.getClaimValue("user"), UserCover.class);

            } catch (InvalidJwtException e) {
                //Logger.getLogger(TokenRepository.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        return usuario;
    }
}
