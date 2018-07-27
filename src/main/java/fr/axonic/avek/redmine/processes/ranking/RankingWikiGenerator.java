package fr.axonic.avek.redmine.processes.ranking;

import fr.axonic.avek.redmine.processes.FreemarkerConfiguration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import javax.mail.MessagingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.time.LocalDate;

public class RankingWikiGenerator {

    public String generateMarkdown(RankingWikiData data) throws IOException {
        Template template = FreemarkerConfiguration.getConfiguration().getTemplate("ranking-wiki.ftl");

        try (ByteArrayOutputStream bytes = new ByteArrayOutputStream()) {
            template.process(data, new OutputStreamWriter(bytes));

            return new String(bytes.toByteArray(), Charset.defaultCharset());
        } catch (TemplateException e) {
            e.printStackTrace();

            return null;
        }
    }

    public static class RankingWikiData {
        private int notFoundPages;
        private int withoutApprovalPages;
        private int nokStructurePages;
        private int nokContentPages;
        private int okContentPages;
        private int totalPages;
        private UsersRanking ranking;
        private LocalDate date;
        private String projectName;

        public int getNotFoundPages() {
            return notFoundPages;
        }

        public void setNotFoundPages(int notFoundPages) {
            this.notFoundPages = notFoundPages;
        }

        public int getWithoutApprovalPages() {
            return withoutApprovalPages;
        }

        public void setWithoutApprovalPages(int withoutApprovalPages) {
            this.withoutApprovalPages = withoutApprovalPages;
        }

        public int getNokStructurePages() {
            return nokStructurePages;
        }

        public void setNokStructurePages(int nokStructurePages) {
            this.nokStructurePages = nokStructurePages;
        }

        public int getNokContentPages() {
            return nokContentPages;
        }

        public void setNokContentPages(int nokContentPages) {
            this.nokContentPages = nokContentPages;
        }

        public int getOkContentPages() {
            return okContentPages;
        }

        public void setOkContentPages(int okContentPages) {
            this.okContentPages = okContentPages;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public UsersRanking getRanking() {
            return ranking;
        }

        public void setRanking(UsersRanking ranking) {
            this.ranking = ranking;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public String getProjectName() {
            return projectName;
        }

        public void setProjectName(String projectName) {
            this.projectName = projectName;
        }
    }
}
