package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {
    @InjectMocks
    SubscriptionService subscriptionService;
    @Mock
    SubscriptionRepository subscriptionRepository;

    long followerId;
    long followeeId;

    @BeforeEach
    public void setUp() {
        followerId = 2;
        followeeId = 1;
    }

    @Test
    public void testAssertThrow() {
        // Mock the behavior of the existsByFollowerIdAndFolloweeId method to return true
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(true);

        // Verify that a DataValidationException is thrown
        assertThrows(DataValidationException.class, () -> subscriptionService.followUser(followerId, followeeId));
    }

    @Test
    public void testFollowUser() {
        // Test case where followerId is not equal to followeeId
        subscriptionService.followUser(followerId, followeeId);

        // Verify that the followUser method in the subscriptionService is called with the correct arguments
        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);
    }

    @Test
    public void testThrow() {
        // Mock the behavior of the existsByFollowerIdAndFolloweeId method
        when(subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)).thenReturn(false).thenReturn(true);

        // Call the followUser method
        subscriptionService.followUser(followerId, followeeId);

        // Verify that the existsByFollowerIdAndFolloweeId method is called with the correct arguments
        verify(subscriptionRepository, times(1)).existsByFollowerIdAndFolloweeId(followerId, followeeId);

        // Verify that the followUser method is called with the correct arguments
        verify(subscriptionRepository, times(1)).followUser(followerId, followeeId);

        // Call the followUser method and expect a DataValidationException to be thrown
        try {
            subscriptionService.followUser(followerId, followeeId);
        } catch (DataValidationException e) {
            // Verify that the right message DataValidationException is thrown
            assertEquals("This subscription already exists", e.getMessage());
        }

        // Verify that the followUser method is not called again
        verifyNoMoreInteractions(subscriptionRepository);
    }

    @Test
    public void testUnfollowUser() {
        // Test case where followerId is not equal to followeeId
        subscriptionService.unfollowUser(followerId, followeeId);

        // Verify that the followUser method in the subscriptionService is called with the correct arguments
        verify(subscriptionRepository, times(1)).unfollowUser(followerId, followeeId);
    }
}