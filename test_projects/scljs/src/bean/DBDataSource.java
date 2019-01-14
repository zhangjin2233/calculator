public class DBDataSource {
    private String url;
    private String userName;
    private String password;

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public String getUserName() {
        return userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
