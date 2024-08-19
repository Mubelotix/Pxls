package space.pxls.auth;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.UnirestException;
import space.pxls.App;

public class CasAuthService extends AuthService {
    public CasAuthService(String id) {
        super(id, App.getConfig().getBoolean("oauth.cas.enabled"), App.getConfig().getBoolean("oauth.cas.registrationEnabled"));
    }

    @Override
    public String getRedirectUrl(String state) {
        System.out.println("Redirecting to CAS login page");
        return App.getConfig().getString("oauth.cas.login_url") + "?service=" + getCallbackUrl() + "&state=" + state;
    }

    @Override
    public String getToken(String token) throws UnirestException {
        System.out.println("Validating token from CAS");

        HttpResponse<String> response = Unirest.get(App.getConfig().getString("oauth.cas.validate_url"))
                .queryString("service", getCallbackUrl())
                .queryString("ticket", token)
                .asString();

        if (response.getStatus() != 200) {
            throw new UnirestException("CAS validation failed: " + response.getStatusText());
        }

        String username;
        try {
            String start_username_field = "<cas:" + App.getConfig().getString("oauth.cas.username_field") + ">";
            String end_username_field = "</cas:" + App.getConfig().getString("oauth.cas.user") + ">";
            username = response.getBody().split(start_username_field)[1].split(end_username_field)[0];
        } catch (Exception e) {
            throw new UnirestException("CAS validation failed: " + e.getMessage());
        }

        return username;
    }

    @Override
    public String getIdentifier(String token) throws UnirestException {
        return token;
    }

    public String getName() {
        return "CAS";
    }

    @Override
    public void reloadEnabledState() {
        this.enabled = App.getConfig().getBoolean("oauth.cas.enabled");
        this.registrationEnabled = App.getConfig().getBoolean("oauth.cas.registrationEnabled");
    }
}
