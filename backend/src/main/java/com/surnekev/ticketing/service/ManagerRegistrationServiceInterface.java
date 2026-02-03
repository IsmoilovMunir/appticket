package com.surnekev.ticketing.service;

/**
 * Interface for manager registration operations
 */
public interface ManagerRegistrationServiceInterface {

    /**
     * Request registration for a new manager account
     * @param username Username for the new manager
     * @param password Password for the new manager
     */
    void requestRegistration(String username, String password);

    /**
     * Confirm registration using verification code
     * @param username Username to confirm
     * @param verificationCode Verification code sent via Telegram
     */
    void confirmRegistration(String username, String verificationCode);

    /**
     * Cleanup expired verification codes
     */
    void cleanupExpiredCodes();
}