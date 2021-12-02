package pl.transport.truck.service.password

import pl.transport.truck.service.md5.Md5Service
import spock.lang.Specification
import spock.lang.Unroll

class PasswordServiceImplTest extends Specification {

    private final PasswordHasher passwordHasher = new PBKDF2PasswordHasher(() -> "pepper")
    private final PasswordService passwordService = new PasswordServiceImpl(passwordHasher, new Md5Service())

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
