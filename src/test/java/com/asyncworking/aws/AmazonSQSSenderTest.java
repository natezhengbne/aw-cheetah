package com.asyncworking.aws;

public class AmazonSQSSenderTest {
    //    @Test
//    public void shouldSendMessageToSQS() throws JsonProcessingException {
//        Message expectedMessage = MessageBuilder.withPayload("payload").build();
//        ArgumentCaptor<String> endpointCaptor = ArgumentCaptor.forClass(String.class);
//        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
//        when(objectMapper.writeValueAsString(any())).thenReturn("payload");
//        emailService.sendMessageToSQS(
//                mockUserEntity,
//                "verificationLink",
//                EmailType.Verification,
//                "test@gmail.com"
//        );
//        verify(queueMessagingTemplate).send(endpointCaptor.capture(), messageCaptor.capture());
//        assertEquals(expectedMessage.getPayload(), messageCaptor.getValue().getPayload());
//    }
}
