package com.ProyectoTecnonet.tecnonet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ProyectoTecnonet.tecnonet.model.Usuario;
import com.ProyectoTecnonet.tecnonet.service.UsuarioService;

import jakarta.validation.Valid;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/registrate")
    public String mostrarFormularioDeRegistro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "registrate";
    }

    @PostMapping("/registrate")
    public String procesarRegistro(@Valid @ModelAttribute("usuario") Usuario usuario,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "registrate";
        }

        try {
            usuarioService.registrarNuevoUsuario(usuario);
            redirectAttributes.addFlashAttribute("mensaje", "¡Te has registrado con éxito! Ya puedes iniciar sesión.");
            return "redirect:/iniciarSesion";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/registrate";
        }
    }
}
