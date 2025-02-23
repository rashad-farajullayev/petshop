package com.thesniffers.service;

import com.thesniffers.security.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.mockito.MockedStatic;

import static org.mockito.Mockito.mockStatic;

public class ServiceTestBase {
    public static final String TENANT_1_SECRET_TOKEN = "tenant1-secret-token-abcdef";
    public static final String CUSTOMER_NAME = "John Doe";
    public static final String CUSTOMER_TIMEZONE = "UTC";
    public static final String BASKET_ITEM_NAME = "Item 1";
    public static final String OTHER_USER_TOKEN = "other-user-token";

    protected MockedStatic<SecurityUtils> securityMock;

    protected void mockCurrentUser() {
        securityMock = mockStatic(SecurityUtils.class);
        securityMock.when(SecurityUtils::getCurrentUserToken).thenReturn(TENANT_1_SECRET_TOKEN);
        securityMock.when(SecurityUtils::isAdmin).thenReturn(false);
    }

    @AfterEach
    void tearDown() {
        // Ensure MockedStatic is properly closed after each test
        if (securityMock != null) {
            securityMock.close();
        }
    }
}
