package com.acuitybotting.security.rabbitmq.api;

import com.acuitybotting.db.arango.acuity.identities.service.AcuityUsersService;
import com.acuitybotting.security.jwt.domain.JwtPrincipal;
import com.acuitybotting.security.rabbitmq.domain.Permission;
import com.acuitybotting.security.rabbitmq.domain.ResourceType;
import com.acuitybotting.security.rabbitmq.domain.api.ResourceCheck;
import com.acuitybotting.security.rabbitmq.domain.api.TopicCheck;
import com.acuitybotting.security.rabbitmq.domain.api.VirtualHostCheck;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;


/**
 * Created by Zachary Herridge on 7/13/2018.
 */
@RestController
@Slf4j
@RequestMapping(path = "/api/rabbitmq/auth", method = {RequestMethod.GET, RequestMethod.POST})
public class RabbitAuthBackendHttpController {

    private static final String REFUSED = "deny";
    private static final String ACCEPTED = "allow";

    private final AcuityUsersService acuityUserService;

    @Autowired
    public RabbitAuthBackendHttpController(AcuityUsersService acuityUserService) {
        this.acuityUserService = acuityUserService;
    }

    @RequestMapping("user")
    public String user(@RequestParam("username") String username, @RequestParam("password") String password) {
        log.info("Trying to authenticate user {}", username);

        if (username.equals("acuity-guest")) return ACCEPTED;

        return acuityUserService.isValidConnectionKey(username, password) ? ACCEPTED + StringUtils.collectionToDelimitedString(Collections.emptyList(), " ", " ", "") : REFUSED;
    }

    @RequestMapping("vhost")
    public String vhost(VirtualHostCheck check) {
        log.info("Checking vhost access with {}", check);
        return "/".equals(check.getVhost()) ? ACCEPTED : REFUSED;
    }

    @RequestMapping("resource")
    public String resource(ResourceCheck check) {
        log.info("Checking resource access with {}", check);

        if (ResourceType.QUEUE.equals(check.getResource()) && check.getName().startsWith("user." + check.getUsername() + ".queue."))
            return ACCEPTED;
        else if (ResourceType.EXCHANGE.equals(check.getResource()) && !Permission.CONFIGURE.equals(check.getPermission()) && check.getName().equals("acuitybotting.general"))
            return ACCEPTED;

        return REFUSED;
    }

    @RequestMapping("topic")
    public String topic(TopicCheck check) {
        log.info("Checking topic access with {}", check);

        if (Permission.CONFIGURE.equals(check.getPermission())) return REFUSED;

        if (check.getUsername().equals("acuity-guest")){
            if (ResourceType.TOPIC.equals(check.getResource()) && check.getName().equals("acuitybotting.general") && check.getRouting_key().startsWith("user." + check.getUsername() + ".services.path-finding.find-path"))
                return ACCEPTED;
            return REFUSED;
        }

        if (ResourceType.TOPIC.equals(check.getResource()) && check.getName().equals("acuitybotting.general") && check.getRouting_key().startsWith("user." + check.getUsername() + "."))
            return ACCEPTED;

        return REFUSED;
    }
}
