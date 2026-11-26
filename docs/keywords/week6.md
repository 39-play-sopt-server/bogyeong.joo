# 1. 세션 기반 인증

> 세션 기반 인증 방식에서 사용자 인증이 발생하는 과정을 다시 한 번 정리해 주세요.
> 로그인 요청의 발생부터, 세션의 생성과 응답, 그리고 이후 요청에서의 검증 과정까지 꼼꼼하게 정리하고, 스스로 이해하는 시간을 가져주세요!

- **세션과 세션 ID** — 서버는 로그인한 사용자의 식별자와 인증 상태를 세션에 보관합니다. 브라우저는 해당 세션을 찾는 데 쓰이는 세션 ID를 쿠키로 보관합니다. [RFC 6265 §8.4](https://www.rfc-editor.org/rfc/rfc6265.html#section-8.4)
- **1단계 · 로그인 요청** — 브라우저가 아이디와 비밀번호를 HTTPS로 전송합니다. 서버는 사용자 계정을 찾고, 입력한 비밀번호를 저장된 비밀번호 해시와 대조합니다. 인증에 실패하면 로그인 응답에서 오류를 반환합니다.
- **2단계 · 세션 생성과 응답** — 인증에 성공하면 서버는 인증 상태를 세션에 저장하고 새 세션 ID를 사용합니다. 로그인 전 세션이 있었다면 ID를 교체합니다. 응답의 `Set-Cookie: JSESSIONID=<세션 ID>; Path=/; HttpOnly; Secure; SameSite=Lax`처럼 세션 ID와 쿠키 속성을 내려주면 브라우저가 이를 저장합니다. `JSESSIONID`는 서블릿 환경에서 쓰이는 이름의 예시입니다. [Jakarta Servlet 세션 명세](https://jakarta.ee/specifications/servlet/6.1/jakarta-servlet-spec-6.1#sessions) · [MDN 세션 관리](https://developer.mozilla.org/en-US/docs/Web/Security/Authentication/Session_management)
- **3단계 · 이후 요청과 검증** — 브라우저가 쿠키의 전송 조건에 맞는 요청에 `Cookie: JSESSIONID=<세션 ID>`를 자동으로 붙입니다. 서버는 ID로 세션을 조회하고, 세션이 만료되지 않았는지와 인증된 사용자가 들어 있는지를 확인합니다. 유효한 세션이면 사용자 식별자를 꺼내고, 요청한 자원에 접근할 권한이 있는지 검사합니다. 세션이 없거나 만료되면 보호된 요청은 다시 인증해야 합니다. [RFC 6265 §3.1](https://www.rfc-editor.org/rfc/rfc6265.html#section-3.1)
- **로그아웃과 만료** — 로그아웃 시 서버는 세션을 무효화하고, 브라우저에는 쿠키를 만료시키는 `Set-Cookie`를 보냅니다. 서버가 여러 대라면 어느 인스턴스에서도 같은 세션을 찾을 수 있도록 Redis 같은 공유 저장소를 사용할 수 있습니다. 브라우저가 인증 쿠키를 자동으로 전송하므로 상태를 바꾸는 요청에는 CSRF 방어도 고려합니다. [MDN 세션 관리](https://developer.mozilla.org/en-US/docs/Web/Security/Authentication/Session_management)

# 2. 토큰 기반 인증과 JWT

> 토큰 기반 인증 방식에서 사용자 인증이 발생하는 과정을 다시 한 번 정리해 주세요.
> 로그인 요청의 발생부터, JWT의 발급과 응답, 기르고 이후 요청에서의 검증과 재발급 과정까지 꼼꼼하게 정리하고, 스스로 이해하는 시간을 가져주세요!

- **JWT(JSON Web Token)** — 사용자 식별자나 만료 시각 같은 클레임을 담는 토큰 형식입니다. 일반적인 서명형 JWT는 `헤더.페이로드.서명`으로 구성됩니다. 서명은 발급 주체와 변조 여부를 검증하는 데 쓰이며, 페이로드는 디코딩해서 읽을 수 있으므로 비밀번호 등 민감한 값을 넣지 않습니다. [RFC 7519](https://www.rfc-editor.org/rfc/rfc7519.html)
- **1단계 · 로그인과 토큰 발급** — 클라이언트가 자격 증명을 보내면 서버가 검증합니다. 성공하면 서버는 사용자 식별자 `sub`, 만료 시각 `exp` 등을 담은 Access Token에 서명해 응답합니다. 재발급을 지원하는 설계에서는 Refresh Token도 발급합니다. Access Token은 API 호출에 쓰고, Refresh Token은 발급 서버에 새 Access Token을 요청할 때 씁니다. Refresh Token에는 JWT나 서버가 상태를 관리하는 무작위 문자열을 사용할 수 있습니다. [RFC 7519 §4.1](https://www.rfc-editor.org/rfc/rfc7519.html#section-4.1) · [RFC 6749 §1.5](https://www.rfc-editor.org/rfc/rfc6749.html#section-1.5)
- **2단계 · 이후 요청과 검증** — 클라이언트는 보호된 API 호출에 `Authorization: Bearer <Access Token>`을 붙입니다. 서버는 허용한 알고리즘과 신뢰하는 키로 서명을 검증하고, 만료 시각과 서비스가 요구하는 발급자 `iss`, 대상 `aud`, 토큰 용도 등을 확인합니다. 검증을 통과한 뒤 사용자와 권한을 확인합니다. JWT 자체를 검증하는 데는 세션 저장소 조회가 필요하지 않지만, 계정 상태나 즉시 폐기 여부를 확인하는 정책에 따라 서버 조회가 추가될 수 있습니다. [RFC 6750 §2.1](https://www.rfc-editor.org/rfc/rfc6750.html#section-2.1) · [RFC 8725 §3](https://www.rfc-editor.org/rfc/rfc8725.html#section-3)
- **3단계 · Access Token 재발급** — Access Token이 만료되면 클라이언트는 재발급 엔드포인트에 Refresh Token을 보냅니다. 서버는 자신이 발급한 토큰인지, 만료되거나 폐기되지는 않았는지 확인합니다. 유효하면 새 Access Token을 발급하고 클라이언트는 새 토큰으로 원래 요청을 다시 보냅니다. Refresh Token도 만료됐거나 폐기됐다면 다시 로그인해야 합니다. [RFC 6749 §6](https://www.rfc-editor.org/rfc/rfc6749.html#section-6)
- **Refresh Token Rotation과 로그아웃** — 회전을 적용하면 재발급할 때마다 새 Refresh Token을 발급하고 직전 토큰을 무효화합니다. 로그아웃 때는 클라이언트가 보관한 토큰을 지우고 서버가 Refresh Token을 폐기합니다. 이미 발급된 Access Token을 즉시 막으려면 서버가 폐기 상태도 검사해야 합니다. 서명과 만료 시각만 확인하는 구조에서는 기존 Access Token이 만료될 때까지 유효할 수 있습니다. [RFC 9700 §4.14.2](https://www.rfc-editor.org/rfc/rfc9700.html#section-4.14.2)

# 3. OAuth 2.0과 소셜 로그인

> OAuth의 인증 프로세스에 대해 다시 한 번 정리해 주세요.
> 클라이언트, 서버, OAuth 서버가 각각 어떤 흐름에 따라 어떤 로직을 수행하는지 꼼꼼하게 정리하고, 스스로 이해하는 시간을 가져주세요!

- **OAuth 2.0과 OpenID Connect(OIDC)** — OAuth 2.0은 사용자가 외부 서비스의 특정 기능이나 데이터에 접근할 권한을 앱에 위임하는 표준입니다. OIDC는 OAuth 2.0 위에 사용자 인증을 추가하며, 서비스는 ID Token을 검증해 누가 로그인했는지 확인합니다. Access Token은 리소스 API 접근에, ID Token은 로그인한 사용자 확인에 사용합니다. [RFC 6749 §1](https://www.rfc-editor.org/rfc/rfc6749.html#section-1) · [OpenID Connect Core §1.3](https://openid.net/specs/openid-connect-core-1_0.html#Overview)
- **참여 주체** — 사용자는 리소스 소유자입니다. 아래 흐름은 웹 백엔드가 OAuth 클라이언트인 경우이며, 브라우저는 로그인 화면과 리다이렉트를 처리합니다. 인가 서버(Authorization Server)는 사용자를 인증하고 코드와 토큰을 발급합니다. 리소스 서버(Resource Server)는 프로필 같은 보호 데이터를 제공하고, 전달받은 Access Token과 권한 범위를 확인합니다. [RFC 6749 §1.1](https://www.rfc-editor.org/rfc/rfc6749.html#section-1.1)
- **1단계 · 인가 요청** — 사용자가 소셜 로그인 버튼을 누르면, 서비스 백엔드는 브라우저를 제공자의 인가 엔드포인트로 리다이렉트합니다. 요청에는 `response_type=code`, `client_id`, 등록된 `redirect_uri`, `scope`, `state`, PKCE의 `code_challenge`가 포함됩니다. `scope`는 요청하는 권한 범위이고 OIDC에서는 `openid`를 포함합니다. 백엔드는 콜백에서 비교할 `state`와 토큰 교환에 쓸 `code_verifier`를 보관합니다. [RFC 6749 §4.1.1](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.1.1) · [RFC 9700 §2.1.1](https://www.rfc-editor.org/rfc/rfc9700.html#section-2.1.1)
- **2단계 · 사용자 인증과 코드 반환** — 인가 서버는 사용자의 기존 로그인 상태를 확인하거나 로그인 화면을 표시하고, 필요한 경우 권한 동의를 받습니다. 이후 브라우저를 `redirect_uri`로 돌려보내며 짧게 유효한 일회용 `code`와 `state`를 전달합니다. 서비스 백엔드는 돌아온 `state`를 자신이 보관한 값과 비교해 해당 로그인 요청의 응답인지 확인합니다. [RFC 6749 §4.1.2](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.1.2)
- **3단계 · 코드와 토큰 교환** — 서비스 백엔드는 제공자의 토큰 엔드포인트에 `code`, `redirect_uri`, `code_verifier`를 보내고, 등록 방식에 따라 클라이언트 인증도 수행합니다. 인가 서버는 코드의 유효기간과 사용 여부, 클라이언트 및 리다이렉트 URI의 일치 여부, PKCE 검증값을 확인합니다. 검증에 성공하면 Access Token을 반환하고, OIDC 흐름에서는 ID Token도 반환합니다. 정책에 따라 Refresh Token을 함께 받을 수 있습니다. [RFC 6749 §4.1.3–4.1.4](https://www.rfc-editor.org/rfc/rfc6749.html#section-4.1.3) · [OpenID Connect Core §3.1.3](https://openid.net/specs/openid-connect-core-1_0.html#TokenEndpoint)
- **4단계 · 사용자 확인과 서비스 로그인** — OIDC를 사용하는 백엔드는 ID Token의 서명, 발급자 `iss`, 대상 `aud`, 만료 시각 `exp` 등을 검증하고, 요청에 `nonce`를 사용했다면 받은 값과 비교합니다. 확인한 `iss`와 `sub`의 조합으로 서비스 계정을 찾거나 가입을 진행합니다. 추가 프로필이 필요하면 제공자의 UserInfo API에 Access Token을 보내고, 응답의 `sub`가 ID Token의 `sub`와 일치하는지 확인합니다. 이후 서비스 자체 세션이나 Access Token을 발급해 서비스 API 요청에 사용합니다. [OpenID Connect Core §3.1.3.7](https://openid.net/specs/openid-connect-core-1_0.html#IDTokenValidation) · [OpenID Connect Core §5.3.2](https://openid.net/specs/openid-connect-core-1_0.html#UserInfoResponse)
