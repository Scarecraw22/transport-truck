package pl.transport.truck.service.password;

public class LocalPepperProvider implements PepperProvider {

    private static final String PEPPER = "209d3u9=-0d2!!d23290d-2da";

    @Override
    public String get() {
        return PEPPER;
    }
}
