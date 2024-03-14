package com.zerobase.cms.user.config.filter;

import com.zerobase.cms.user.client.service.seller.SellerService;
import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.domain.domain.common.UserVo;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@WebFilter(urlPatterns = "/seller/*") // /customer/* 인 uri 에 대해 필터를 건다
@RequiredArgsConstructor
public class SellerFilter implements Filter {
    private final JwtAuthenticationProvider jwtAuthenticationProvider;
    private final SellerService sellerService;

    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        String token = req.getHeader("X-AUTH-TOKEN");

        // 만료일이 다된 토큰인지 확인
        if(!jwtAuthenticationProvider.validateToken(token)){
            throw new ServletException("Invalid Access");
        }

        UserVo vo = jwtAuthenticationProvider.getUserVo(token);

        // 괜찮은 토큰인지를 토큰에 있는 ID, 이메일들로 확인
        sellerService.findByIdAndEmail(vo.getId(), vo.getEmail())
                .orElseThrow(() -> new ServletException("Invalid access"));

        // filter 에서 내보내는 과정
        filterChain.doFilter(servletRequest, servletResponse);
        System.out.println("ServletRequest: "+((HttpServletRequest) servletRequest).getHeader("X-AUTH-TOKEN"));
        System.out.println("ServletResponse: "+servletResponse.getContentType());
    }
}
