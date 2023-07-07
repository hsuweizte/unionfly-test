# 聯翔有限公司-Java工程師筆試題目 

用Spring Framework & MyBatis & MySQL & RabbitMQ & Redis 實作以下功能 
1. 實作一個登入API，當帳號密碼與DB的一致時，Access Token，不一致時，回傳錯誤訊息， 
2. 將Access Token存進Redis，多重登入時使用後踢前的策略 
3. 使用Pub/Sub方式，將用戶登入的結果記錄在DB或Log File 
4. 實作一個個人資訊 REST API，當Access Token可用時，回傳用戶的顯示名稱，不可用時，回傳錯誤訊息 
5. 實作一個登出 API，讓Access Token失效 



> 思路：實作一個攔截器，當攔截到登入請求時時，先解析Authorization的JWT，若JWT為空，則放行登入
> 進入到Controller時，在SERVER端產生一個JWT放到header裡的Authorization。當第二次登入時，Filter去解析header裡的
> Authorization的JWT token，如果不一樣，驗證異常
 
> 

```java
public class AuthTokenFilter extends OncePerRequestFilter {
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            //解析請求HEADER裡的Authorization
            String jwt = parseJwt(request);
            //如果JWT 有值 且TOKEN一致，且驗證成功
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                String username = jwtUtils.getUserNameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e);
        }

        filterChain.doFilter(request, response);
    }

    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }

        return null;
    }
}

```

```Java
 @Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
    //綁定yaml
    @Value("${hsuweizte.app.jwtSecret}")
    private String jwtSecret;

    @Value("${hsuweizte.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    public String generateJwtToken(Authentication authentication) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key key() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    //token去解析失敗時會丟出 exception
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(key()).build().parse(authToken);
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}

```