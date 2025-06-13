package sve2.beecon

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.Resource
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.springframework.web.servlet.resource.PathResourceResolver
import org.springframework.web.servlet.resource.ResourceResolverChain

@Configuration
class StaticResourceConfig(private val webProperties: WebProperties): WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**")
            .addResourceLocations(*webProperties.resources.staticLocations)
            .resourceChain(webProperties.resources.chain.isCache)
            .addResolver(ResourceResolverWithFallbackToIndex())
    }
}

internal class ResourceResolverWithFallbackToIndex : PathResourceResolver() {
    override fun resolveResourceInternal(
        request: HttpServletRequest?,
        requestPath: String,
        locations: List<Resource>,
        chain: ResourceResolverChain
    ): Resource? {
        val resolved: Resource? = super.resolveResourceInternal(request, requestPath, locations, chain)
        if (resolved != null) {
            return resolved
        }

        if (requestPath.startsWith("api/")) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "${request?.method} $requestPath"
            ) // don't forward API calls to index.html because they are not to be handled by frontend routing
        }

        return super.resolveResourceInternal(request, "index.html", locations, chain)
    }

}
