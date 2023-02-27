package fr.uga.l3miage.library.authors;

import fr.uga.l3miage.data.domain.Author;
import fr.uga.l3miage.library.books.BookDTO;
import fr.uga.l3miage.library.books.BooksMapper;
import fr.uga.l3miage.library.service.AuthorService;
import fr.uga.l3miage.library.service.DeleteAuthorException;
import fr.uga.l3miage.library.service.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class AuthorsController {

    private final AuthorService authorService;
    private final AuthorMapper authorMapper;
    private final BooksMapper booksMapper;

    @Autowired
    public AuthorsController(AuthorService authorService, AuthorMapper authorMapper, BooksMapper booksMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
        this.booksMapper = booksMapper;
    }

    @GetMapping("/authors")
    public Collection<AuthorDTO> authors(@RequestParam(value = "q", required = false) String query) {
        Collection<Author> authors;
        if (query == null) {
            authors = authorService.list();
        } else {
            authors = authorService.searchByName(query);
        }
        return authors.stream()
                .map(authorMapper::entityToDTO)
                .toList();
    }

    @GetMapping("/authors/{id}") //Chemin de l'api pour appeler cette fonction
    public AuthorDTO author(@PathVariable("id") Long id) throws EntityNotFoundException{
        //gère mois les exeptions
        try{
            Author auteur = authorService.get(id);//On rérupère l'auteur en passant par le service authorService
            return authorMapper.entityToDTO(auteur); //On "parse" l'auteur en type DTO 
        }
        catch(EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    //creation d'un auteur
    @PostMapping("/authors")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorDTO newAuthor(@RequestBody AuthorDTO author) {
        Author auteur=authorService.save(authorMapper.dtoToEntity(author));
        return authorMapper.entityToDTO(auteur);
    }

    //mise à jour d'un auteur
    @PutMapping("/authors/{id}")
    public AuthorDTO updateAuthor(@RequestBody AuthorDTO author,@PathVariable("id") Long id) throws EntityNotFoundException{
        if(author.id()==id){
            authorService.update(authorMapper.dtoToEntity(author));
        }
        else if(author.id()==null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        // attention AuthorDTO.id() doit être égale à id, sinon la requête utilisateur est mauvaise
        return author;
    }

    //suppression d'un auteur
    @DeleteMapping("/authors/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable("id") Long id) {
        try{
            authorService.delete(id);
        }
        catch (EntityNotFoundException | DeleteAuthorException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }      // unimplemented... yet!
    }

    public Collection<BookDTO> books(Long authorId) {
        return Collections.emptyList();
    }

}
