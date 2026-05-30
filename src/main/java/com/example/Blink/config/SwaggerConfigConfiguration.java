package com.example.Blink.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfigConfiguration {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ShortLink Platform API")
                        .version("1.0")
                        .description(
                                "RESTful API for the ShortLink Platform." +

                                        "This platform provides a complete URL shortening solution with analytics, security, administration, and developer APIs.  " +

                                        "Key Features:  " +

                                        "🔐 Authentication & User Management " +
                                        "- User registration and login " +
                                        "- JWT authentication and refresh tokens " +
                                        "- Password reset and profile management " +
                                        "- Secure password encryption using BCrypt  " +

                                        "🔗 URL Management " +
                                        "- Generate shortened URLs " +
                                        "- Custom aliases for branded links " +
                                        "- URL expiration support " +
                                        "- Edit and delete existing links " +
                                        "- Bulk URL shortening via CSV/Excel uploads  " +

                                        "📊 Analytics & Tracking " +
                                        "- Click tracking and visitor insights " +
                                        "- Country, browser, OS, and device analytics " +
                                        "- Real-time analytics dashboard " +
                                        "- Dynamic charts and reporting  " +

                                        "🛡️ Security Features " +
                                        "- JWT-secured APIs " +
                                        "- Rate limiting and abuse protection " +
                                        "- Password-protected URLs " +
                                        "- Malware and suspicious URL detection  " +

                                        "📱 QR Code Services " +
                                        "- Automatic QR code generation " +
                                        "- Downloadable QR images for shortened links  " +

                                        "⚡ Performance & Caching " +
                                        "- Redis-powered URL caching " +
                                        "- Fast redirection with minimal latency " +
                                        "- Configurable cache expiration policies  " +

                                        "📧 Notifications " +
                                        "- Link expiration alerts " +
                                        "- Login activity notifications " +
                                        "- Suspicious activity detection alerts  " +

                                        "👨‍💼 Administration " +
                                        "- User management and moderation " +
                                        "- Abuse reporting and URL blocking " +
                                        "- System monitoring dashboards " +
                                        "- Platform health and metrics tracking  " +

                                        "📨 Background Processing " +
                                        "- Asynchronous analytics processing " +
                                        "- Queue-based event handling " +
                                        "- Automatic retry and dead-letter queue support  " +

                                        "🧑‍💻 Developer APIs " +
                                        "- Public URL shortening APIs " +
                                        "- API key authentication " +
                                        "- Interactive Swagger/OpenAPI documentation  " +

                                        "📈 Monitoring & Infrastructure " +
                                        "- Centralized logging and tracing " +
                                        "- Error monitoring and alerting " +
                                        "- Docker-ready deployment " +
                                        "- CI/CD pipeline integration " +
                                        "- Horizontal scalability support  " +

                                        "All API responses follow a standardized response structure and are fully documented through Swagger/OpenAPI."
                        )
                        .contact(new Contact()
                                .name("Abdulrahman Ahmed")
                                .email("abdulraman.ahmedd@gmail.com")
                        )
                );
    }
}