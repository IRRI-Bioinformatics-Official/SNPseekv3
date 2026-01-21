package user.ui.module;

import com.microsoft.aad.msal4j.*;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

public class MicrosoftAuth {

    public static void main(String[] args) {
        // Prefer environment variables for configuration
        String CLIENT_ID = System.getenv("MICROSOFT_CLIENT_ID");
        String TENANT_ID = System.getenv("MICROSOFT_TENANT_ID");
        String REDIRECT_URI = System.getenv("MICROSOFT_REDIRECT_URI");

        if (CLIENT_ID == null || TENANT_ID == null || REDIRECT_URI == null) {
            System.err.println("MicrosoftAuth configuration missing. Please set MICROSOFT_CLIENT_ID, MICROSOFT_TENANT_ID and MICROSOFT_REDIRECT_URI as environment variables before running this program.");
            return;
        }

        final String AUTHORITY = "https://login.microsoftonline.com/" + TENANT_ID;

        try {
            // Build the PublicClientApplication
            PublicClientApplication publicClientApplication = PublicClientApplication
                    .builder(CLIENT_ID)
                    .authority(AUTHORITY)
                    .build();

            // Define the required scope(s)
            Set<String> scopes = Collections.singleton("User.Read"); // Using Set instead of List

            // Acquire the token interactively (browser-based login)
            InteractiveRequestParameters requestParameters = InteractiveRequestParameters
                    .builder(new URI(REDIRECT_URI))
                    .scopes(scopes)  // Using Set of scopes correctly
                    .build();

            IAuthenticationResult result = publicClientApplication
                    .acquireToken(requestParameters)
                    .join();  // This is a blocking call for simplicity

            // Successfully authenticated, retrieve the access token
            String accessToken = result.accessToken();
            System.out.println("Access token acquired (length=" + (accessToken!=null?accessToken.length():0) + ")");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}