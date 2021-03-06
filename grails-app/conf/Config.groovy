import org.apache.log4j.PatternLayout
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.LoggingAppender
import org.pillarone.riskanalytics.core.log.TraceAppender
import org.pillarone.riskanalytics.core.output.batch.calculations.MysqlCalculationsBulkInsert
import org.pillarone.riskanalytics.core.output.batch.results.MysqlBulkInsert
import org.pillarone.riskanalytics.core.output.batch.results.OracleBulkInsert
import org.pillarone.riskanalytics.core.output.batch.results.SQLServerBulkInsert
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.external.ExternalValuesExtendedResource
import org.pillarone.riskanalytics.domain.pc.cf.claim.generator.external.ExternalValuesResource

grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.types = [html: ['text/html', 'application/xhtml+xml'],
        xml: ['text/xml', 'application/xml'],
        text: 'text-plain',
        js: 'text/javascript',
        rss: 'application/rss+xml',
        atom: 'application/atom+xml',
        css: 'text/css',
        csv: 'text/csv',
        all: '*/*',
        json: ['application/json', 'text/json'],
        form: 'application/x-www-form-urlencoded',
        multipartForm: 'multipart/form-data'
]
// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"

grails.spring.bean.packages = ['org.pillarone']

// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true

maxIterations = 100000
keyFiguresToCalculate = null
resultBulkInsert = null
userLogin = false
// a cron for a batch, A cron expression is a string comprised of 6 or 7 fields separated by white space.
// Fields can contain any of the allowed values: Sec Min Hour dayOfMonth month dayOfWeek Year
// Fire every 60 minutes
batchCron = "0 0/10 * * * ?"
includedResources = [ExternalValuesExtendedResource, ExternalValuesResource]
environments {
    development {
        models = ["CoreModel", 'ApplicationModel', 'MultiProductStatutoryLifeModel', 'PodraModel']

        ExceptionSafeOut = System.out
        log4j = {
            info 'org.pillarone.riskanalytics.core.output',
                    'org.pillarone.riskanalytics.core.components',
                    'org.pillarone.riskanalytics.core.simulation',
                    'org.pillarone.modelling.fileimport',
                    'org.pillarone.modelling.domain',
                    'org.pillarone.modelling.packets',
                    'org.pillarone.riskanalytics.core.simulation.engine',
                    'org.pillarone.riskanalytics.core.parameterization'

            debug 'org.pillarone.modelling.output',
                    'org.pillarone.modelling.packets.life.UnitLinkedLifeReinsuranceContract'

            warn()
        }
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
                'pdf': 200
        ]
    }
    test {
        ExceptionSafeOut = System.out
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
                'pdf': 200
        ]
    }
    sqlserver {
        models = ["CoreModel", 'ApplicationModel', 'MultiProductStatutoryLifeModel']
        resultBulkInsert = SQLServerBulkInsert
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
        ]
        log4j = {
            info 'org.pillarone.riskanalytics.core.output',
                    'org.pillarone.riskanalytics.core.components',
                    'org.pillarone.riskanalytics.core.simulation',
                    'org.pillarone.modelling.fileimport',
                    'org.pillarone.modelling.domain',
                    'org.pillarone.modelling.packets',
                    'org.pillarone.riskanalytics.core.parameterization',
                    'org.pillarone.application.jobs.JobScheduler',
                    'org.pillarone.riskanalytics.core.simulation.engine',
                    'org.pillarone.application.jobs.BatchRunner'
        }
    }
    mysql {
        resultBulkInsert = MysqlBulkInsert
        calculationBulkInsert = MysqlCalculationsBulkInsert
        ExceptionSafeOut = System.out
        models = ['GIRAModel',  'ORSAModel']

        log4j = {
            appenders {

                String layoutPattern = "[%d{dd.MMM.yyyy HH:mm:ss,SSS}] - %t (%X{username}) - %-5p %c{1} %m%n"

                console name: 'stdout', layout: pattern(conversionPattern: layoutPattern)

                LoggingAppender loggingAppender = LoggingAppender.getInstance()
                loggingAppender.setName('application')
                loggingAppender.loggingManager.layout = "[%d{HH:mm:ss,SSS}] - %c{1} %m%n"
                appender loggingAppender

                TraceAppender traceAppender = new TraceAppender(name: "traceAppender")
                traceAppender.layout = new PatternLayout("[%d{dd.MMM.yyyy HH:mm:ss,SSS}] - %-5p %c{1} %m%n")
                appender traceAppender

            }
            root {
                error()
                additivity = false
            }

            def infoPackages = [
                    'org.pillarone.riskanalytics',
            ]

            def debugPackages = [
                    'org.pillarone.riskanalytics.core.fileimport'
            ]

            info(
                    traceAppender: infoPackages,
                    application: infoPackages,
                    stdout: infoPackages,
                    additivity: false
            )

            debug(
                    traceAppender: ['org.pillarone.riskanalytics.core.simulation.item.ParametrizedItem', 'org.pillarone.riskanalytics.application.ui.parameterization.model'],
                    application: debugPackages,
                    stdout: debugPackages,
                    additivity: false
            )

        }
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
                'pdf': 200
        ]
    }
    oracle {
//        serverSessionPrefix = ";jsessionid="
        resultBulkInsert = OracleBulkInsert
//        calculationBulkInsert = org.pillarone.riskanalytics.core.output.batch.calculations.GenericBulkInsert
        ExceptionSafeOut = System.out
        models = ['PodraModel']
        log4j = {
            appenders {

                String layoutPattern = "[%d{dd.MMM.yyyy HH:mm:ss,SSS}] - %t (%X{username}) - %-5p %c{1} %m%n"

                console name: 'stdout', layout: pattern(conversionPattern: layoutPattern)

                LoggingAppender loggingAppender = LoggingAppender.getInstance()
                loggingAppender.setName('application')
                loggingAppender.loggingManager.layout = "[%d{HH:mm:ss,SSS}] - %c{1} %m%n"
                appender loggingAppender

            }
            root {
                error()
                additivity = false
            }

            def infoPackages = [
                    'org.pillarone.riskanalytics',
            ]

            def debugPackages = [
                    'org.pillarone.riskanalytics.core.fileimport'
            ]

            info(
                    application: infoPackages,
                    stdout: infoPackages,
                    additivity: false
            )

            debug(
                    application: debugPackages,
                    stdout: debugPackages,
                    additivity: false
            )

        }
        keyFiguresToCalculate = [
                'stdev': true
        ]
    }
    production {
        userLogin = true
        maxIterations = 10000
        models = ["CoreModel", 'ApplicationModel', 'MultiProductStatutoryLifeModel']
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
                'pdf': 200
        ]
    }
    standalone {
        ExceptionSafeOut = System.err
        maxIterations = 10000
        models = ["CoreModel", 'ApplicationModel', 'MultiProductStatutoryLifeModel']
        keyFiguresToCalculate = [
                'stdev': true,
                'percentile': [0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0],
                'var': [99, 99.5],
                'tvar': [99, 99.5],
                'pdf': 200
        ]
    }
}

log4j = {
    appenders {
        console name: 'stdout', layout: pattern(conversionPattern: '[%d] %-5p %c{1} %m%n')
        file name: 'file', file: 'RiskAnalytics.log', layout: pattern(conversionPattern: '[%d] %-5p %c{1} %m%n')
    }
    root {
        error 'stdout', 'file'
        additivity = false
    }
    error 'org.codehaus.groovy.grails.web.servlet',  //  controllers
            'org.codehaus.groovy.grails.web.pages', //  GSP
            'org.codehaus.groovy.grails.web.sitemesh', //  layouts
            'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
            'org.codehaus.groovy.grails.web.mapping', // URL mapping
            'org.codehaus.groovy.grails.commons', // core / classloading
            'org.codehaus.groovy.grails.plugins', // plugins
            'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
//        'org.springframework',
//        'org.hibernate',
            'org.pillarone.modelling.fileimport',
            'org.pillarone.modelling.ui.util.ExceptionSafe',
            'org.pillarone.riskanalytics.core.wiring',
            'org.pillarone.modelling.domain',
            'org.pillarone.modelling.util'
    info()
    debug()
    warn()
}

// Uncomment and edit the following lines to start using Grails encoding & escaping improvements

/* remove this line 
// GSP settings
grails {
    views {
        gsp {
            encoding = 'UTF-8'
            htmlcodec = 'xml' // use xml escaping instead of HTML4 escaping
            codecs {
                expression = 'html' // escapes values inside null
                scriptlet = 'none' // escapes output from scriptlets in GSPs
                taglib = 'none' // escapes output from taglibs
                staticparts = 'none' // escapes output from static template parts
            }
        }
        // escapes all not-encoded output at final stage of outputting
        filteringCodecForContentType {
            //'text/html' = 'html'
        }
    }
}
remove this line */
