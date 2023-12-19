package messenger;

import edu.messenger.Database.DatabaseHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class DatabaseHandlerTest {
    private static final Path PATH = Paths.get("ForTest.s3db");

    @Test
    @DisplayName("Database creation Test")
    void shouldCreateDatabase() throws SQLException, ClassNotFoundException, IOException {
        if (Files.exists(PATH)) {
            Files.delete(PATH);
        }
        assertThat(Files.exists(PATH)).isFalse();
        DatabaseHandler.getDbConnection("ForTest");
        assertThat(Files.exists(PATH)).isTrue();
        assertThrows(SQLException.class, () -> DatabaseHandler.signUpUser("test", "test", "test"));
    }

    @Test
    @DisplayName("Tables Creation Test")
    void shouldCreateTablesInDatabase() throws IOException, SQLException, ClassNotFoundException {
        if (Files.exists(PATH)) {
            Files.delete(PATH);
        }
        DatabaseHandler.getDbConnection("ForTest");
        DatabaseHandler.createDatabase();
        assertDoesNotThrow(() -> DatabaseHandler.signUpUser("test", "test", "test"));
    }

    @Test
    @DisplayName("User Addition Test")
    void shouldAddUserToDatabase() throws IOException, SQLException, ClassNotFoundException {
        if (Files.exists(PATH)) {
            Files.delete(PATH);
        }
        DatabaseHandler.getDbConnection("ForTest");
        DatabaseHandler.createDatabase();
        assertThat(DatabaseHandler.signUpUser("test", "test", "test")).isTrue();
        List<String> users = DatabaseHandler.getAllUsers();
        assertThat(users.size()).isEqualTo(1);
        assertThat(DatabaseHandler.signUpUser("test", "test", "test")).isFalse();
        users = DatabaseHandler.getAllUsers();
        assertThat(users.size()).isEqualTo(1);
        assertThat(DatabaseHandler.signUpUser("test2", "test", "test")).isTrue();
        users = DatabaseHandler.getAllUsers();
        assertThat(users.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Chat Addition Test")
    void shouldAddChatToDatabase() throws IOException, SQLException, ClassNotFoundException {
        if (Files.exists(PATH)) {
            Files.delete(PATH);
        }
        DatabaseHandler.getDbConnection("ForTest");
        DatabaseHandler.createDatabase();
        assertThat(DatabaseHandler.signUpChat("test", "test", "test")).isFalse();
        assertThat(DatabaseHandler.signUpUser("test", "test", "test")).isTrue();
        assertThat(DatabaseHandler.signUpUser("test2", "test2", "test2")).isTrue();
        assertThat(DatabaseHandler.signUpChat("test", "test2", "")).isTrue();
        Map<Integer, List<String>> chats = DatabaseHandler.getAllChats();
        assertThat(chats.size()).isEqualTo(1);
        assertThat(DatabaseHandler.signUpChat("test2", "test", "")).isFalse();
        chats = DatabaseHandler.getAllChats();
        assertThat(chats.size()).isEqualTo(1);
        assertThat(DatabaseHandler.signUpUser("test3", "test3", "test3")).isTrue();
        assertThat(DatabaseHandler.signUpChat("test3", "test2", "")).isTrue();
        chats = DatabaseHandler.getAllChats();
        assertThat(chats.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("Chat Change Test")
    void shouldChangeChatInDatabase() throws IOException, SQLException, ClassNotFoundException {
        if (Files.exists(PATH)) {
            Files.delete(PATH);
        }
        DatabaseHandler.getDbConnection("ForTest");
        DatabaseHandler.createDatabase();
        assertThat(DatabaseHandler.signUpChat("test", "test", "test")).isFalse();
        assertThat(DatabaseHandler.signUpUser("test", "test", "test")).isTrue();
        assertThat(DatabaseHandler.signUpUser("test2", "test2", "test2")).isTrue();
        assertThat(DatabaseHandler.signUpChat("test", "test2", "123")).isTrue();
        assertThat(DatabaseHandler.changeChat("test3", "test", "321")).isFalse();
        assertThat(DatabaseHandler.getTextFromChat("test2", "test")).isEqualTo("123");
        assertThat(DatabaseHandler.changeChat("test", "test2", "321")).isTrue();
        assertThat(DatabaseHandler.getTextFromChat("test2", "test")).isEqualTo("123\n321");
    }
}
