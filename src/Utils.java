import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Utils {
    static Scanner teclado = new Scanner(System.in);

    public static Connection conectar() {
        Properties props = new Properties();
        props.setProperty("user", "hyago");
        props.setProperty("password", "acesso123");
        props.setProperty("ssl", "false");
        String URL_SERVIDOR = "jdbc:postgresql://localhost:5432/jpostgresql";
        try {
            return DriverManager.getConnection(URL_SERVIDOR, props);
        } catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                System.err.println("Verifique o Driver de Conexão");
            } else {
                System.err.println("Verifique se o server está ativo");
            }
            System.exit(-42);
            return null;
        }
    }

    public static void desconectar(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void listar() {
        String BUSCAR_TODOS = "SELECT * FROM produtos";
        try {
            Connection conn = conectar();
            PreparedStatement produtos = conn.prepareStatement(
                    BUSCAR_TODOS,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );
            ResultSet res = produtos.executeQuery();
            res.last();
            int qtd = res.getRow();
            res.beforeFirst();

            if (qtd > 0) {
                System.out.println("Listando produtos ...");
                System.out.println("------------------");
                while(res.next()) {
                    System.out.println("ID: " + res.getInt(1));
                    System.out.println("Produto: " + res.getString(2));
                    System.out.println("Preço: " + res.getFloat(3));
                    System.out.println("Estoque: " + res.getInt(4));
                    System.out.println("------------------");
                }
            } else {
                System.out.println("Não existem produtos cadastrados.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro Buscando os produtos");
            System.exit(-42);
        }
    }

    public static void inserir() {
        System.out.println("Informe o nome do Produto: ");
        String nome = teclado.nextLine();
        System.out.println("Informe o preço: ");
        float preco = teclado.nextFloat();
        System.out.println("Informe a quantidade em estoque: ");
        int estoque = teclado.nextInt();

        String INSERIR = "INSERT INTO produtos(nome, preco, estoque) VALUES (?, ?, ?)";

        try {
            Connection conn = conectar();
            PreparedStatement salvar = conn.prepareStatement(INSERIR);
            salvar.setString(1, nome);
            salvar.setFloat(2, preco);
            salvar.setInt(3, estoque);
            salvar.executeUpdate();
            salvar.close();
            desconectar(conn);
            System.out.println("O produto  " + nome + " foi inserido com sucesso!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao salvar o produto.");
            System.exit(-42);
        }
    }

    public static void atualizar() {
        System.out.println("Informe o código do produto: ");
        int id = Integer.parseInt(teclado.nextLine());

        String BUSCAR_POR_ID = "SELECT * FROM produtos WHERE id=?";
        try {
            Connection conn = conectar();
            PreparedStatement produto = conn.prepareStatement(
                    BUSCAR_POR_ID,
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );
            produto.setInt(1, id);
            ResultSet res = produto.executeQuery();

            res.last();
            int qtd =  res.getRow();
            res.beforeFirst();

            if(qtd > 0 ){
                System.out.println("Informe o nome do Produto: ");
                String nome = teclado.nextLine();
                System.out.println("Informe o preço do Produto: ");
                float preco = teclado.nextFloat();
                System.out.println("Informe a quantidade em estoque: ");
                int estoque = teclado.nextInt();

                String ATUALIZAR = "UPDATE produtos SET nome=?, preco=?, estoque=? WHERE id=?";
                PreparedStatement upd = conn.prepareStatement(ATUALIZAR);

                upd.setString(1, nome);
                upd.setFloat(2, preco);
                upd.setInt(3,estoque);
                upd.setInt(4, id);

                upd.executeUpdate();
                upd.close();
                desconectar(conn);
                System.out.println("O produto " + nome + " foi atualizado com sucesso.");
            }else {
                System.out.println("Não existe produto com o id informado.");
            }
        }catch (Exception e){
            System.err.println("Não foi possivel atualizar o produto.");
            System.exit(-42);
        }
    }

    public static void deletar() {
        String DELETAR = "DELETE FROM produtos WHERE id=?";
        String BUSCAR_POR_ID = "SELECT * FROM produtos WHERE id=?";

        System.out.println("informe o código do produto: ");
        int id = Integer.parseInt(teclado.nextLine());

        try {
            Connection conn = conectar();
            PreparedStatement produto = conn.prepareStatement(
                    BUSCAR_POR_ID,
                    ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_READ_ONLY
            );

            produto.setInt(1, id);
            ResultSet res = produto.executeQuery();

            res.last();
            int qtd = res.getRow();
            res.beforeFirst();

            if(qtd > 0 ){
                PreparedStatement del = conn.prepareStatement(DELETAR);
                del.setInt(1, id);
                del.executeUpdate();
                del.close();
                desconectar(conn);
                System.out.println("O produto foi deletado com sucesso!");
            }else {
                System.out.println("Não existe produto com o id informado");
            }

        }catch (Exception e){
            e.printStackTrace();
            System.err.println("Erro ao deletar produto");
            System.exit(-42);
        }
    }

    public static void menu() {
        System.out.println("------------Gerenciamento de Produtos---------------");
        System.out.println("Selecione uma opção");
        System.out.println("1 - Listar Produtos");
        System.out.println("2 - Inserir Produtos");
        System.out.println("3 - Atualizar Produtos");
        System.out.println("4 - Deletar Produtos");

        int op = Integer.parseInt(teclado.nextLine());
        switch (op) {
            case 1:
                listar();
                break;
            case 2:
                inserir();
                break;
            case 3:
                atualizar();
                break;
            case 4:
                deletar();
                break;
            default:
                System.out.println("Opção invalida");
                break;
        }
    }
}
