package com.wktransportservices.fx.business.manager.chat

import com.fasterxml.jackson.databind.ObjectMapper
import com.squareup.okhttp.*
import com.transwide.utils.config.ConfigUtils
import com.wktransportservices.fx.dto.UserDTO
import com.wktransportservices.fx.dto.chat.ChatUserDTO
import com.wktransportservices.fx.exception.chat.ChatClientException
import com.wktransportservices.fx.utils.PropertyUtils
import org.springframework.http.HttpStatus
import spock.lang.Specification

class ChatClientTest extends Specification {

    static final CHAT_CLIENT_URL = "http://chat-client-url"
    static final CHAT_API_KEY = "anApiKey"

    static final CHAT_USER = ChatUserDTO.fromUserDTO(new UserDTO())
    static final CHAT_USER_AS_JSON = "chat-user-as-json"

    static final REQUEST = new Request.Builder()
            .url("https://www.wtransnet.com")
            .build()

    static final RESPONSE_BODY_AS_STRING = "response-body-as-string"

    ChatClient chatClient = new ChatClient()

    OkHttpClient client = Mock()
    Call call = Mock()
    ObjectMapper mapper = Mock()

    def setup() {

        chatClient.client = client
        chatClient.mapper = mapper

        ConfigUtils.ENVIRONMENT_PROPERTIES.setProperty(PropertyUtils.CHAT_HOST_URL, CHAT_CLIENT_URL)
        ConfigUtils.ENVIRONMENT_PROPERTIES.setProperty(PropertyUtils.CHAT_API_KEY, CHAT_API_KEY)
    }

    def 'userProvisioning'() {

        given:
        def response = createOkResponse()

        when:
        def result = chatClient.userProvisioning(CHAT_USER)

        then:
        1 * mapper.writeValueAsString(CHAT_USER) >> CHAT_USER_AS_JSON
        1 * client.newCall(_ as Request) >> call
        1 * call.execute() >> response
        1 * mapper.readValue(RESPONSE_BODY_AS_STRING, String.class) >> RESPONSE_BODY_AS_STRING

        and:
        result == RESPONSE_BODY_AS_STRING
    }

    def 'userProvisioning throws ChatClientException when response from chat is not successful'() {

        given:
        def response = createErrorResponse()

        when:
        chatClient.userProvisioning(CHAT_USER)

        then:
        1 * mapper.writeValueAsString(CHAT_USER) >> CHAT_USER_AS_JSON
        1 * client.newCall(_ as Request) >> call
        1 * call.execute() >> response
        1 * mapper.readValue(RESPONSE_BODY_AS_STRING, String.class) >> RESPONSE_BODY_AS_STRING

        and:
        def ex = thrown(ChatClientException)
        ex.status == response.code()
        ex.message == RESPONSE_BODY_AS_STRING
    }

    def 'userProvisioning throws ChatClientException when a generic Exception occurs'() {

        given:
        String exceptionMessage = "fake exception message"

        when:
        chatClient.userProvisioning(CHAT_USER)

        then:
        1 * mapper.writeValueAsString(CHAT_USER) >> { throw new Exception(exceptionMessage) }
        0 * client.newCall(_)
        0 * call.execute()
        0 * mapper.readValue(_ ,_)

        and:
        def ex = thrown(ChatClientException)
        ex.status == HttpStatus.INTERNAL_SERVER_ERROR.value()
        ex.message == exceptionMessage
    }

    private static Response createOkResponse() {
        createResponse(HttpStatus.OK)
    }

    private static Response createErrorResponse() {
        createResponse(HttpStatus.NOT_FOUND)
    }

    private static Response createResponse(HttpStatus status) {

        def responseBody =
                ResponseBody.create(MediaType.parse("application/json; charset=utf-8"), RESPONSE_BODY_AS_STRING)

        new Response.Builder()
                .request(REQUEST)
                .protocol(Protocol.HTTP_1_0)
                .code(status.value())
                .body(responseBody)
                .build()
    }

}