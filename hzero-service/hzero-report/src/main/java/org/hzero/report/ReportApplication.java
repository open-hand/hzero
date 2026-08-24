package org.hzero.report;

import org.hzero.autoconfigure.report.EnableHZeroReport;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;

import com.bstek.ureport.console.UReportServlet;

/**
 *
 * HZERO 报表平台
 *
 * @author xianzhi.chen@hand-china.com 2018年11月27日下午1:36:40
 */
@EnableHZeroReport
@EnableDiscoveryClient
@SpringBootApplication
@ImportResource(value = "classpath:ureport-console-context.xml")
public class ReportApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReportApplication.class, args);
    }
}
