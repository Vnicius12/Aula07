package br.usjt.ads.arqdes.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.usjt.ads.arqdes.model.entity.Filme;
import br.usjt.ads.arqdes.model.entity.Genero;
import br.usjt.ads.arqdes.model.service.FilmeService;
import br.usjt.ads.arqdes.model.service.GeneroService;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class ManterFilmesController {
	
	@Autowired
	private ServletContext servletContext;
	@Autowired
	private FilmeService fService;
	@Autowired
	private GeneroService gService;
	
	
	@RequestMapping("/")
	public String inicio() {
		return "index";
	}
	
	@RequestMapping("/inicio")
	public String inicio1() {
		return "index";
	}
	
	@RequestMapping("/listar_filmes")
	public String listarFilmes(HttpSession session){
		session.setAttribute("lista", null);
		return "ListarFilmes";
	}
	
	@RequestMapping("/novo_filme")
	public String novoFilme(HttpSession session) {
		try {
			List<Genero> generos = gService.listarGeneros();
			session.setAttribute("generos", generos);
			return "CriarFilme";
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "index";
	}
	
	@RequestMapping("/inserir_filme")
	public String inserirFilme(@Valid Filme filme, BindingResult result, Model model) {
		try {
			if(!result.hasFieldErrors("titulo")) {
				Genero genero = gService.buscarGenero(filme.getGenero().getId());
				filme.setGenero(genero);
				model.addAttribute("filme", filme);
				fService.inserirFilme(filme);
				return "VisualizarFilme";
			} else {
				return "CriarFilme";
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "index";
	}

	@RequestMapping("/buscar_filmes")
	public String buscarFilmes(HttpSession session, @RequestParam String chave){
		try {
			List<Filme> lista;
			if (chave != null && chave.length() > 0) {
				lista = fService.listarFilmes(chave);
			} else {
				lista = fService.listarFilmes();
			}
			session.setAttribute("lista", lista);
			return "ListarFilmes";
		} catch (IOException e) {
			e.printStackTrace();
			return "Erro";
		}
	}
	
	@RequestMapping("incluir_local")
	public String inclusao(@Valid Local local, BindingResult result, Model model,
			@RequestParam("file")MultipartFile file) {
		try {
			if(result.hasErrors()) {
				List<Tipo> tipos = ts.listarTipos();
				model.addAttribute("tipos, tipos");
				return "local/localcriar";
			}
			ls.criar(local);
			ls.gravarImagem(servletContext, local, file);
			return "redirect:listar_locais";
		}catch (IOException e) {
			e.printStackTrace();
			model.addAttribute("erro", e);
		}
		return "erro";
	}
	
	public void gravarImagem(ServletContext servletContext, Local local, MultipartFile file)
			throws IOException{
		if(!file.isEmpty()) {
			BufferedImage src = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
			String path = servletContext.getRealPath(servletContext. getContextPath());
			path = path.substring(0, path.lastIndexOf('/'));
			String nomeArquivo = "img" + local.getId()+".jpg";
			(local).setImagem(nomeArquivo);
			atualizar(local);
			File destination = new File(path + File.separatorChar + "img" + File.separatorChar +
					nomeArquivo);
			if(destination.exists()) {
				destination.delete();
			}
			ImageIO.write(src, "img", destination);
		}
	}
	@RequestMapping("atualizar_local")
	public String atualizar(Local local, Model model, @RequestParam("file") MultipartFile file ) {
		try {
			ls.atualizar(local);
			ls.gravarImagem(servletContext, local, file);
			return "redirect:listar_locais"; 
		}catch(IOException e){
			e.printStackTrace();
			model.addAttribute("erro", e);
		}
		return "erro";
	}
}

















