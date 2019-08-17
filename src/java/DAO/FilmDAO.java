
package DAO;

import beans.Film;
import beans.Uloge;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import util.DB;

public class FilmDAO {
    
    public static boolean dodajFilm(String originalniNaziv, String nazivNaSrpskom, 
            int godinaIzdanja, String filmOpis, int idReziser, int idZemljePorekla, 
            int trajanjeFilma, String imdbLink, String poster, int idGlumac) {
        
        String filmSql = "insert into film(originalniNaziv, nazivNaSrpskom, godinaIzdanja, filmOpis, idReziser, idZemljePorekla, trajanjeFilma, imdbLink, poster) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String ulogeSql = "insert into uloge(idFilm, idGlumac) values (?, ?)";
        try {
            Connection connection = DB.otvoriKonekciju();
            PreparedStatement ps = connection.prepareStatement(filmSql, Statement.RETURN_GENERATED_KEYS);
               
            ps.setString(1, originalniNaziv);
            ps.setString(2, nazivNaSrpskom);
            ps.setInt(3, godinaIzdanja);
            ps.setString(4, filmOpis);
            ps.setInt(5, idReziser);
            ps.setInt(6, idZemljePorekla);
            ps.setInt(7, trajanjeFilma);
            ps.setString(8, imdbLink);
            ps.setString(9, poster);
            
            ps.executeUpdate();
            
            int idFilm = -1;
            ResultSet rsId = ps.getGeneratedKeys();
            if (rsId.next()){
                idFilm=rsId.getInt(1);
            }
            
            PreparedStatement ps2 = connection.prepareStatement(ulogeSql);
            
            ps2.setInt(1,idFilm);
            ps2.setInt(2,idGlumac);
            
            ps2.executeUpdate();
            
            
            
            ps.close();
            ps2.close();
            connection.close();
                
            return true;
            
        } 
        catch (SQLException ex) {
            Logger.getLogger(FilmDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public static List<Film> ispisFilmaZaKorisnika(String originalniNazivFilma) throws SQLException{
        List<Film> listaFilmova = new ArrayList<Film>();
        String glumci = "";
        String sql1 = "select * from film f, uloge u, glumci g "
                + "where f.idFilm = u.idFilm and u.idGlumac = g.idGlumac and f.originalniNaziv = ?;";
        try (Connection connection = DB.otvoriKonekciju();
            PreparedStatement ps = connection.prepareStatement(sql1);){
            ps.setString(1, originalniNazivFilma);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                glumci += rs.getString("imeGlumci") + ",";
            }
        }
        glumci = glumci.substring(0, glumci.length()-1);
        String sql = "select * from film f, reziseri r, zemlje_porekla z where f.idReziser = r.idReziser "
                + "and f.idZemljePorekla = z.idZemljePorekla and f.originalniNaziv = ?";
        try (Connection connection = DB.otvoriKonekciju();
            PreparedStatement ps = connection.prepareStatement(sql);){
            ps.setString(1, originalniNazivFilma);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Film film = new Film();
                film.setNazivNaSrpskom(rs.getString("nazivNaSrpskom"));
                film.setOriginalniNaziv(rs.getString("originalniNaziv"));
                film.setPoster(rs.getString("Poster"));
                film.setGodinaIzdanja(rs.getInt("godinaIzdanja"));
                film.setFilmOpis(rs.getString("filmOpis"));
                film.setNazivReziser(rs.getString("imeReziseri"));
                film.setTrajanjeFilma(rs.getInt("trajanjeFilma"));
                film.setZemljaPorekla(rs.getString("imeZemljaPorekla"));
                film.setImdbLink(rs.getString("imdbLink"));
                film.setSviGlumciFilma(glumci);
                listaFilmova.add(film);
                
            }
        }
            
        
        return listaFilmova;
    }
}
