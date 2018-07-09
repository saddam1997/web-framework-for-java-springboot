/*******************************************************************************
 * Copyright (C) 2017 http://bndy.net
 * Created by Bendy (Bing Zhang)
 ******************************************************************************/
package net.bndy.wf.controller;

import net.bndy.wf.ApplicationContext;
import net.bndy.wf.exceptions.UnauthorizedException;
import org.aspectj.util.FileUtil;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/admin")
public class AdminController extends _BaseController {


    @RequestMapping(value = "/")
    public String home(Model model) throws IOException, UnauthorizedException {

        if (ApplicationContext.userNoRoles()) {
            throw new UnauthorizedException();
        }

        model.addAttribute("skin", applicationConfig.getAdminSkin());
        model.addAttribute("locale", LocaleContextHolder.getLocale().toString());

        File resourcesRoot = new ClassPathResource("/").getFile();

        List<String> jsFiles = new ArrayList<>();
        File angularDirectiveDir = new ClassPathResource("/static/apps/admin/lib/directives").getFile();
        // load all angularjs directives
        for (File f : FileUtil.listFiles(angularDirectiveDir, (pathname) -> pathname.getName().toLowerCase().endsWith(".js"))) {
            jsFiles.add(f.getAbsolutePath().replace(resourcesRoot.getAbsolutePath(), "/")
                // fix path separator issue on WinOS
                .replace("\\", "/")
                .replace("//", "/")
            );
        }

        File rootModule = new ClassPathResource("/static/apps/admin/modules").getFile();

        // load all js files in modules
        for (File f : FileUtil.listFiles(rootModule,
            (pathname) -> pathname.getName().toLowerCase().endsWith(".js"))) {

            jsFiles.add(f.getAbsolutePath().replace(resourcesRoot.getAbsolutePath(), "/")
                // fix path separator issue on WinOS
                .replace("\\", "/")
                .replace("//", "/")
            );
        }
        model.addAttribute("jsFiles", jsFiles);

        // load all html files as modules
        List<String> modules = new ArrayList<>();
        for (File f : FileUtil.listFiles(rootModule, (pathname -> pathname.getName().toLowerCase().endsWith(".html")))) {
            String moduleName = f.getPath()
                .replace(".html", "")
                .replace(rootModule.getAbsolutePath(), "")
                .replaceAll("[\\\\/]+", "-")
                .replaceAll("^-", "");

            modules.add(moduleName);
        }
        model.addAttribute("modules", modules);

        return "admin/index";
    }
}
