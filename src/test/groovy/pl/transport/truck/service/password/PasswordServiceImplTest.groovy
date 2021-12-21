package pl.transport.truck.service.password

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder
import pl.transport.truck.service.md5.Md5Service
import spock.lang.Specification
import spock.lang.Unroll

class PasswordServiceImplTest extends Specification {

    private final PasswordEncoder passwordEncoder = new Pbkdf2PasswordEncoder("secret")
    private final PasswordService passwordService = new PasswordServiceImpl(passwordEncoder, new Md5Service())

    @Unroll
    def "#input password is properly encoded"() {
        when:
        def result = passwordService.encode(input, salt)

        then:
        assert passwordService.matches(input, salt, result)

        where:
        input            | salt
        "samplePassword" | "salt1"
        "Ant!@fafsa"     | "salt2"
        "ooosoasoa!0sfa" | "sfafasf121er1"
    }
}
