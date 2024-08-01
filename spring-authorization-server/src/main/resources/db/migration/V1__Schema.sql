CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- refer to oauth2-authorization-consent-schema.sql
CREATE TABLE
    oauth2_authorization_consent (
        registered_client_id VARCHAR(100) NOT NULL,
        principal_name VARCHAR(200) NOT NULL,
        authorities VARCHAR(1000) NOT NULL,
        PRIMARY KEY (registered_client_id, principal_name)
    );

-- refer to oauth2-registered-client-schema.sql
CREATE TABLE
    oauth2_registered_client (
        id VARCHAR(100) NOT NULL,
        client_id VARCHAR(100) NOT NULL,
        client_id_issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
        client_secret VARCHAR(200) DEFAULT NULL,
        client_secret_expires_at TIMESTAMP DEFAULT NULL,
        client_name VARCHAR(200) NOT NULL,
        client_authentication_methods VARCHAR(1000) NOT NULL,
        authorization_grant_types VARCHAR(1000) NOT NULL,
        redirect_uris VARCHAR(1000) DEFAULT NULL,
        post_logout_redirect_uris VARCHAR(1000) DEFAULT NULL,
        scopes VARCHAR(1000) NOT NULL,
        client_settings VARCHAR(2000) NOT NULL,
        token_settings VARCHAR(2000) NOT NULL,
        PRIMARY KEY (id)
    );

-- refer to oauth2-authorization-schema.sql
CREATE TABLE
    oauth2_authorization (
        id VARCHAR(100) NOT NULL,
        registered_client_id VARCHAR(100) NOT NULL,
        principal_name VARCHAR(200) NOT NULL,
        authorization_grant_type VARCHAR(100) NOT NULL,
        authorized_scopes VARCHAR(1000) DEFAULT NULL,
        attributes TEXT DEFAULT NULL,
        state VARCHAR(500) DEFAULT NULL,
        authorization_code_value TEXT DEFAULT NULL,
        authorization_code_issued_at TIMESTAMP DEFAULT NULL,
        authorization_code_expires_at TIMESTAMP DEFAULT NULL,
        authorization_code_metadata TEXT DEFAULT NULL,
        access_token_value TEXT DEFAULT NULL,
        access_token_issued_at TIMESTAMP DEFAULT NULL,
        access_token_expires_at TIMESTAMP DEFAULT NULL,
        access_token_metadata TEXT DEFAULT NULL,
        access_token_type VARCHAR(100) DEFAULT NULL,
        access_token_scopes VARCHAR(1000) DEFAULT NULL,
        oidc_id_token_value TEXT DEFAULT NULL,
        oidc_id_token_issued_at TIMESTAMP DEFAULT NULL,
        oidc_id_token_expires_at TIMESTAMP DEFAULT NULL,
        oidc_id_token_metadata TEXT DEFAULT NULL,
        refresh_token_value TEXT DEFAULT NULL,
        refresh_token_issued_at TIMESTAMP DEFAULT NULL,
        refresh_token_expires_at TIMESTAMP DEFAULT NULL,
        refresh_token_metadata TEXT DEFAULT NULL,
        user_code_value TEXT DEFAULT NULL,
        user_code_issued_at TIMESTAMP DEFAULT NULL,
        user_code_expires_at TIMESTAMP DEFAULT NULL,
        user_code_metadata TEXT DEFAULT NULL,
        device_code_value TEXT DEFAULT NULL,
        device_code_issued_at TIMESTAMP DEFAULT NULL,
        device_code_expires_at TIMESTAMP DEFAULT NULL,
        device_code_metadata TEXT DEFAULT NULL,
        PRIMARY KEY (id)
    );

CREATE TABLE
    users (
        id UUID PRIMARY KEY DEFAULT uuid_generate_v4 (),
        username VARCHAR(50) NOT NULL UNIQUE,
        first_name VARCHAR(50) NOT NULL,
        last_name VARCHAR(50) NOT NULL,
        gender VARCHAR(1) NOT NULL,
        birthday VARCHAR(8) NOT NULL,
        password VARCHAR(100) NOT NULL,
        pwd_reset_date TIMESTAMP NOT NULL,
        auth_provider VARCHAR(50) NOT NULL,
        created_by UUID NOT NULL,
        created_on TIMESTAMP NOT NULL,
        updated_by UUID,
        updated_on TIMESTAMP,
        enabled BOOLEAN NOT NULL DEFAULT true
    );

CREATE TABLE
    role (
        id UUID PRIMARY KEY DEFAULT uuid_generate_v4 (),
        name VARCHAR(50) NOT NULL UNIQUE
    );

CREATE TABLE
    user_role (
        user_id UUID NOT NULL,
        role_id UUID NOT NULL,
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
        FOREIGN KEY (role_id) REFERENCES role (id) ON DELETE CASCADE,
        PRIMARY KEY (user_id, role_id)
    );