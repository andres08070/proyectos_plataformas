package com.didamartials.proyecto.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private SeguridadInterceptor seguridadInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(seguridadInterceptor)
                // 1. ¿A QUÉ RUTAS APLICAR SEGURIDAD? (A todas)
                .addPathPatterns("/**")
                
                // 2. ¿QUÉ RUTAS SON PÚBLICAS? (Excluir de la seguridad)
                .excludePathPatterns(
                    "/",                
                    "/login",           // Mostrar Login (GET)
                    "/iniciarSesion",   // Procesar Login (POST)
                    "/registro",        // Mostrar Registro (GET)
                    "/registrar",       // Procesar Registro (POST) <--- ¡ESTA ES NUEVA!
                    "/verificar",       // Procesar Verificación (POST) <--- ¡ESTA ES NUEVA!
                    // ... Archivos estáticos
                    "/css/**",         
                    "/js/**",          
                    "/imgs/**"         
                    // ... y cualquier otra ruta pública que tengas.
            );
    }
}