package com.group17.comic.configurations;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 *  - Origin bao gồm giao thức (HTTP hoặc HTTPS), tên miền và cổng. Hai origin được xem là khác nhau nếu bất
 * kỳ thành phần nào trong origin khác nhau
 *  - Same Origin Policy là một chính sách quy định nội dung của một origin chỉ được đọc 
 * và thay đổi bới một thành phần khác cũng nằm cùng origin đó, các yêu cầu từ 
 * các origin khác sẽ bị chặn lại(Hiểu đơn giản là không cho phép request từ domain khác).
 *  => Ví dụ: các thành phần trong http://example.vn:8080 có thể đọc và thay đổi thành phần khác 
 * trong example.vn:8080 đó. Nếu http://example.vn:3000 hay domain nào khác hay thậm chí 
 * các subdomain(VD: http://www.api.example.vn) cần muốn truy xuất hay thay đối thành phần của
 * http://example.vn:8080 thì sẽ bị chặn và nhận lỗi "'No Access-Control-Allow-Origin' header ..."
 *  - Nhưng trong lập trình Web, lập trình viên thường xuyên phải thực hiện truy vấn đến 
 * các domain khác, đặc biệt là khi làm việc với các API. Đó là lúc chúng ta cần đến CORS
 *  - Có 3 cách implement:
 *   + C1: Tạo một @Configuration class implement interface WebMvcConfigurer và override phương
 * thức addCorsMappings(Registry registry). Hoặc tạo một @Bean method trả về WebMvcConfigurer instance 
 * sử dụng anonymous class để override phương thức addCorsMappings(Registry).
 *   + C2: Tạo một @Bean method trả về FilterRegistrationBean, tạo UrlBasedCorsConfigurationSource để định nghĩa
 * cấu hình CORS, sau đó tạo CorsConfiguration íntanse để cấu hình và gán cho UrlBasedCorsConfigurationSource, 
 * Tạo FilterRegistrationBean với tham số CorsFilter sử dụng source để áp dụng CORS, đồng thời thiết lập độ ưu
 * tiên cao nhất cho FilterRegistrationBean để nó được áp dụng trước các filter khác.
 *   + C3: Tạo một @Component class extends OncePerRequestFilter ghi đè phương thức doFilterInternal(request, response, filterChain),
 *  gọi hàm setHeader của response và gọi filterChain.doFilter(request, response) để áp dụng
 */

@Configuration
public class CorsConfig {
    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001"));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*"); // Accept all methods
        // config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        source.registerCorsConfiguration("/**", config);  // Accept all routes
        // source.registerCorsConfiguration("/user/**", config); // Only accept user routes
        FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return bean;
    }
}
