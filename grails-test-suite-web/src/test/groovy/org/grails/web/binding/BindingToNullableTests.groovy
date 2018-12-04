package org.grails.web.binding

import grails.testing.gorm.DomainUnitTest
import grails.testing.web.controllers.ControllerUnitTest
import spock.lang.Ignore
import spock.lang.Issue
import spock.lang.Specification
import grails.artefact.Artefact
import grails.gorm.annotation.Entity

/**
 * @author Graeme Rocher
 * @since 1.0
 */
class BindingToNullableTests extends Specification implements ControllerUnitTest<NullBindingPersonController>, DomainUnitTest<NullBindingPerson> {

    @Issue("https://github.com/grails/grails-core/issues/11188")
    @Ignore
    void testDataBindingBlankStringToNull() {
        controller.params.name = "fred"
        controller.params.dateOfBirth = ''

        when:
        def model = controller.update()

        then:
        controller.response.redirectedUrl != null
    }

    @Issue("https://github.com/grails/grails-core/issues/11188")
    @Ignore
    void testDataBindingToNull() {
        controller.params.name = "fred"
        controller.params.dateOfBirth = 'invalid'

        when:
        def model = controller.update()

        then:
        !controller.response.redirectedUrl
        model.personInstance.name == "fred"
        model.personInstance.hasErrors()
        model.personInstance.errors.getFieldError("dateOfBirth").code == "typeMismatch"
    }
}

@Entity
class NullBindingPerson {
    String name
    Date dateOfBirth

    static constraints = {
        dateOfBirth nullable: true
    }
}

@Artefact('Controller')
class NullBindingPersonController {

    def update = {
        def p = new NullBindingPerson()
        p.properties = params
        if (p.hasErrors()) {
            [personInstance:p]
        }
        else {
            redirect action:"foo"
        }
    }
}

