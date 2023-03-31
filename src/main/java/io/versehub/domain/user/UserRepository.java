package io.versehub.domain.user;

import io.versehub.domain.user.model.UserProfile;
import io.versehub.domain.user.model.UserTopNetGains;
import io.versehub.domain.user.model.UserTopStreak;

import java.util.Collection;
import java.util.concurrent.CompletionStage;

public interface UserRepository {

    CompletionStage<Collection<UserTopStreak>> getUserTopStreak();

    CompletionStage<Collection<UserTopNetGains>> getUserTopNetGains();

    CompletionStage<Integer> getNonceByAddress(String address);

    CompletionStage<UserProfile> generateNonceByAddress(UserProfile userProfile);

    CompletionStage<String> getAddressByNonce(Integer nonce);

    CompletionStage<UserProfile> getUserByAddress(String address);

    CompletionStage<UserProfile> updateUsernameAndAvatarId(UserProfile updatedUserProfile);

    CompletionStage<UserProfile> generateReferralLink(UserProfile updatedUserProfile);

    CompletionStage<String> getAddressByReferralLink(String referralLink);

    CompletionStage<String> getReferralLinkByAddress(String address);

    CompletionStage<Boolean> checkUserValidForReferral(String address);
}

