INSERT INTO
    public.oauth2_registered_client (
        id,
        client_id,
        client_id_issued_at,
        client_secret,
        client_secret_expires_at,
        client_name,
        client_authentication_methods,
        authorization_grant_types,
        redirect_uris,
        scopes,
        client_settings,
        token_settings
    )
VALUES
    (
        'add17e76-548f-4b59-a575-93875eeba478',
        'test-client',
        now (),
        '{noop}secret',
        NULL,
        'add17e76-548f-4b59-a575-93875eeba478',
        'client_secret_basic,client_secret_post',
        'refresh_token,client_credentials,authorization_code',
        'http://localhost:8080/login/oauth2/code/test-client,http://localhost:8080/authorized',
        'openid,read,write',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.client.require-proof-key":false,"settings.client.require-authorization-consent":true}',
        '{"@class":"java.util.Collections$UnmodifiableMap","settings.token.reuse-refresh-tokens":true,"settings.token.id-token-signature-algorithm":["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],"settings.token.authorization-code-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-time-to-live":["java.time.Duration",300.000000000],"settings.token.access-token-format":{"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat","value":"self-contained"},"settings.token.refresh-token-time-to-live":["java.time.Duration",3600.000000000]}'
    );

-- Insert users into the users table
INSERT INTO
    users (
        id,
        username,
        first_name,
        last_name,
        gender,
        birthday,
        password,
        pwd_reset_date,
        auth_provider,
        created_by,
        created_on,
        enabled
    )
VALUES
    (
        uuid_generate_v4 (),
        'ilovecorea@gmail.com',
        'Ricky',
        'Kang',
        'M',
        '19800101',
        '{noop}secret',
        now (),
        'Local',
        uuid_generate_v4 (),
        now (),
        true
    ),
    (
        uuid_generate_v4 (),
        'nauccika@icloud.com',
        'Heedong',
        'Kang',
        'M',
        '19900101',
        '{noop}secret',
        now (),
        'Local',
        uuid_generate_v4 (),
        now (),
        true
    );

INSERT INTO role (id, name)
VALUES (uuid_generate_v4 (), 'ROLE_USER'),
       (uuid_generate_v4 (), 'ROLE_ADMIN');

INSERT INTO user_role (user_id, role_id)
VALUES
    ((SELECT id FROM users WHERE username = 'ilovecorea@gmail.com'), (SELECT id FROM role WHERE name = 'ROLE_USER')),
    ((SELECT id FROM users WHERE username = 'ilovecorea@gmail.com'), (SELECT id FROM role WHERE name = 'ROLE_ADMIN')),
    ((SELECT id FROM users WHERE username = 'nauccika@icloud.com'), (SELECT id FROM role WHERE name = 'ROLE_USER'));