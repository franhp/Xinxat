<?php

/**
 * Test Class per al client de Xinxat
 * 
 * Joc de proves de caixa blanca que serveixen per
 * comprovar el comportament de les funcions:
 *  - de registre, autenticacio del client, i el.liminació d'usuari
 *  - d'edició del perfil
 *  - de comunicació entre client i servidor
 *  - d'administració de sales
 *
 * @author Hector Costa Guzman
 * @version 1.0
 */
class test {

    var $login;
    var $chat;
    var $testUser;
    var $testPassword;
    var $testEmail;
    var $roomName;
    var $roomDescription;
    var $newRoomName;
    var $newRoomDescription;

    public function __construct() {

        $this->login = new Login(); //Login és una extensió de Users
        $this->chat = new Chat();
        echo "<h1>Tests de caixa blanca del prototip del client xinxat</h1>";
    }

    public function runAllTest($testUser, $testPassword, $testEmail, $roomName, $roomDescription, $newRoomName, $newRoomDescription) {
        $this->testUser = $testUser;
        $this->testPassword = $testPassword;
        $this->testEmail = $testEmail;
        $this->roomName = $roomName;
        $this->roomDescription = $roomDescription;
        $this->newRoomName = $newRoomName;
        $this->newRoomDescription = $newRoomDescription;

        echo "<h4>Preparing test...</h4>";
        $this->deleteUserTest();
        echo "<br>";
        $this->deleteRoomTest();

        echo "<h2>Tests d'usuari</h2>";

        echo "<h3>Test de registre usuari</h3>";
        $this->registerUserTest();
        echo "<h3>Test  de login d'usuari</h3>";
        $this->loginTest();
        echo "<h3>Test de presencia d'usuari</h3>";
        $this->presenceTest();
        echo "<h3>Test d'edicio de perfil d'usuari</h3>";
        $this->editProfileTest();
        echo "<h3>Test de logout d'usuari</h3>";
        $this->logoutTest();
        echo "<h3>Test d' eliminacio d'usuari</h3>";
        $this->deleteUserTest();


        echo "<h2>Tests de sales</h2>";

        echo "<h4>Preparant el test, utilitzant usuari antispam...</h4>";

        echo "<h3>Test de crear una sala</h3>";
        $this->createRoomTest();
        echo "<h3>Test d'actualitzar una sala</h3>";
        $this->updateRoomTest();
        echo "<h3>Test que llista totes les sales de la base de dades</h3>";
        $this->listRoomTest();
        echo "<h3>Test insertar un usuari a una sala</h3>";
        $this->insertUserInRoomTest();
        echo "<h3>Test de borrar un usuari d'una sala</h3>";
        $this->removeUserFromRoomTest();
        echo "<h3>Test que llista les sales privades on es un usuari</h3>";
        $this->userInRoomsTest();
        echo "<h3>Test que llista les sales privades on no hi es l'usuari</h3>";
        $this->userOutFromRoomsTest();

        echo "<h3>Test que esborra una sala</h3>";
        $this->deleteRoomTest();


        echo "<h2>Tests del client</h2>";

        echo "<h4>Preparant el test...</h4>";        
        $this->testUser = "testuser";
        $this->testPassword = "qwe123";
        
        $this->loginTest();
        echo "<br>";
        $this->createRoomTest();
        echo "<br>";
        $this->updateRoomTest();

        echo "<h3>Test on un usuari entra a una sala</h3>";
        $this->enterRoomTest();
        echo "<h3>Test on un usuari surt d'una sala</h3>";
        $this->leaveRoomTest();
        echo "<h3>Test que envia un missatge de sala</h3>";
        $this->sendGroupMessagesTest();
        echo "<h3>Test que envia un missatge privat</h3>";
        $this->sendPrivateMessagesTest();
        echo "<h3>Test que executa diferentes comandes contra el servidor</h3>";
        $this->sendCommandsTest();
        echo "<h3>Test que mostra tots els missatges de la pila d'un ussuari</h3>";
        $this->getMessagesTest();
    }

    /**  REGISTRAR USUARI  */
    private function registerUserTest() {

        echo "Creating test user: Values are Name->$this->testUser , Password->$this->testPassword , Email->$this->testEmail, Oauth = null: ";

        if ($this->login->createUser($this->testUser, $this->testPassword, $this->testEmail, null))
            echo "<span style='color: green; font-weight: bold;'>Success</span>";

        else
            echo "<span style='color: red; font-weight: bold;'>Fail</span>";
    }

    /**  LOGIN , LOGOUT i PRESENCIA */
    private function loginTest() {

        echo "Loggin with existing user: Values are Name->$this->testUser , Password->$this->testPassword: ";

        if ($this->login->doLogin($this->testUser, $this->testPassword))
            echo "<span style='color: green; font-weight: bold;'>Success</span>";

        else
            echo "<span style='color: red; font-weight: bold;'>Fail</span>";
    }

    private function presenceTest() {

        echo "Is the user '$this->testUser' logged?: ";

        if ($this->login->isLogged())
            echo "<span style='color: green; font-weight: bold;'>Success</span>";

        else
            echo "<span style='color: red; font-weight: bold;'>Fail</span>";
    }

    private function logoutTest() {

        echo "Logout current user '$this->testUser' : ";

        if ($this->login->doLogout())
            echo "<span style='color: green; font-weight: bold;'>Success</span>";

        else
            echo "<span style='color: red; font-weight: bold;'>Fail</span>";
    }

    /* BORRAR USUARI */

    private function deleteUserTest() {

        echo "Deleting user '$this->testUser': ";

        if ($this->login->deleteUser($this->login->getUserId($this->testUser)))
            echo "<span style='color: green; font-weight: bold;'>Success</span>";

        else
            echo "<span style='color: red; font-weight: bold;'>Fail</span>";
    }

    /** PERFIL */
    private function editProfileTest() {

        $userid = $this->login->getUserId($this->testUser);

        echo "Get the current user '$this->testUser' profile : <br>";

        print_r($this->login->getUserInfo($this->login->getUserId($userid)));

        echo "<br>Updating current logged user (test) name->Testname, lastname->TestLastname, birthday->null, location->TestCity: ";

        if ($this->login->updateUser("TestName", "TestLastname", null, "TestCity"))
            echo "<span style='color: green; font-weight: bold;'>Success</span>";

        else
            echo "<span style='color: red; font-weight: bold;'>Fail</span>";

        echo "<br>Get the current user '$this->testUser' profile again:<br> ";

        print_r($this->login->getUserInfo($userid));
    }

    /** CREAR SALES, EDITAR-LES, BORRAR-LES I LLISTARL-LES */
    private function createRoomTest() {

        echo "Creating sample room in database. Values are Name->$this->roomName , Description->$this->roomDescription: ";

        if ($this->chat->createRoom($this->roomName, $this->roomDescription))
            echo "<span style='color: green; font-weight: bold;'>Success</span>";

        else
            echo "<span style='color: red; font-weight: bold;'>Fail</span>";
    }

    private function updateRoomTest() {

        $roomid = $this->chat->getRoomId($this->roomName);

        echo "<br>Updating the room '$this->roomName' from database: New values are Name->$this->newRoomName , Description->$this->newRoomDescription :";

        if ($this->chat->updateRoom($roomid, $this->newRoomName, $this->newRoomDescription))
            echo "<span style='color: green; font-weight: bold;'>Success</span>";

        else
            echo "<span style='color: red; font-weight: bold;'>Fail</span>";
    }

    private function deleteRoomTest() {

        //s'ha d'haver cridat la funció updateRoomTest anteriorment i runUserTest

        $roomid = $this->chat->getRoomId($this->newRoomName);


        echo "Deleting the room '$this->newRoomName' from database: ";

        if ($this->chat->deleteRoom($roomid))
            echo "<span style='color: green; font-weight: bold;'>Success</span>";

        else
            echo "<span style='color: red; font-weight: bold;'>Fail</span>";
    }

    private function listRoomTest() {

        echo "List of the current rooms in database: <br>";

        print_r($this->chat->listRooms());
    }

    /** AFEGIR UN USUARI A UNA SALA I BORRAR-LO */
    private function insertUserInRoomTest() {

        //s'ha d'haver cridat la funció updateRoomTest anteriorment i runUserTest

        $roomid = $this->chat->getRoomId($this->newRoomName);
        $userid = $this->login->getUserId($this->testUser);

        echo "Inserting user $this->testUser in the $this->newRoomName room: ";

        if ($this->chat->insertUserRoom($roomid, $userid))
            echo "<span style='color: green; font-weight: bold;'>Success</span>";

        else
            echo "<span style='color: red; font-weight: bold;'>Fail</span>";
    }

    private function removeUserFromRoomTest() {

        //s'ha d'haver cridat la funció updateRoomTest anteriorment  i runUserTest

        $roomid = $this->chat->getRoomId($this->newRoomName);
        $userid = $this->login->getUserId($this->testUser);

        echo "Removing user $this->testUser from the $this->newRoomName room: ";

        if ($this->chat->removeUserRoom($roomid, $userid))
             echo "<span style='color: green; font-weight: bold;'>Success</span>";

        else
            echo "<span style='color: red; font-weight: bold;'>Fail</span>";
    }

    /** COMPROVAR SALES ON ES UN USUARI I ON NO */
    private function userInRoomsTest() {

        $userid = $this->login->getUserId($this->testUser);

        echo "List rooms where is $this->testUser : <br>";

        print_r($this->chat->listUserRooms($userid));
    }

    private function userOutFromRoomsTest() {

        $userid = $this->login->getUserId($this->testUser);

        echo "List rooms where isn't $this->testUser : <br>";

        print_r($this->chat->listUserNoRooms($userid));
    }

    /** ENTRAR I SORTIR D'UNA SALA DE XAT 
     * 
     * Totes les funcions prenen com a usuari d'origen el que està ONLINE en aquell moment
     * a més s'ha d'haver cridat la funció updateRoomTest anteriorment per fer les proves 
     * de sales
     */
    private function enterRoomTest() {

        //el servidor ha de retornar OK si entres a una sala o CANT si no tens permis
        echo "User $this->testUser is joining into $this->newRoomName :";

        echo $this->chat->sendCommand("/join " . $this->newRoomName, $this->newRoomName);
    }

    private function leaveRoomTest() {
        //el servidor ha de retornar OK si surts d'una sala o CANT si has sigut assignat 
        //per l'administrador a ella, ja que en aquest cas no pots sortir-ne

        echo "User $this->testUser is leaving room $this->newRoomName :";

        echo $this->chat->sendCommand("/leave", $this->newRoomName);
    }

    /** ENVIAR MISSATGES DE GRUP, PRIVATS i altres COMANDES AL XAT */
    private function sendGroupMessagesTest() {
        //Enviar missatge a una sala de la que formes part
        //si no en formes part no podràs enviar el missatge a no ser
        //que la sala sigui publica
        echo "User $this->testUser is sending groupmessage to $this->newRoomName room:";
        //ha de retornar OK si s'envia correctament
        echo $this->chat->sendMessage($this->testUser, $this->newRoomName, "Group Test message");
    }

    private function sendPrivateMessagesTest() {
        //Enviar missatge a un usuari en privat, en aquest cas l'enviem al mateix usuari d'origen
        echo "User $this->testUser is sending privatemessage to himself:";
        //ha de retornar OK si s'envia correctament
        echo $this->chat->sendMessage($this->testUser, $this->testUser, "Private Test message");
    }

    private function sendCommandsTest() {

        //altres comandes
        echo "User $this->testUser is requesting userlist from @sinmarketing room:";
        echo "<b>".$this->chat->sendCommand("/list @sinmarketing", $this->testUser)."</b>";

        echo "<br>User $this->testUser is inviting himself to the non-private room @imagine:";
        echo "<b>".$this->chat->sendCommand("/invite " . $this->testUser, "@imagine")."</b>";

        echo "<br>User $this->testUser is requesting userlist from @imagine room:";
        echo "<b>".$this->chat->sendCommand("/list @imagine", $this->testUser)."</b>";

        echo "<br>User $this->testUser kick himself from the non-private room @imagine:";
        echo "<b>".$this->chat->sendCommand("/kick " . $this->testUser . " non-reason", "@imagine")."</b>";

        echo "<br>User $this->testUser is requesting userlist from @imagine room again:";
        echo "<b>".$this->chat->sendCommand("/list @imagine", $this->testUser)."</b>";

        echo "<br>User $this->testUser is joining into @imagine after being kicked:";
        echo "<b>".$this->chat->sendCommand("/join @imagine", "@imagine")."</b>";

        echo "<br>User $this->testUser ban himself from the non-private room @imagine:";
        echo "<b>".$this->chat->sendCommand("/ban " . $this->testUser . " non-reason", "@imagine")."</b>";

        echo "<br>User $this->testUser is trying joining into @imagine after being kicked:";
        echo "<b>".$this->chat->sendCommand("/join @imagine", "@imagine")."</b>";

        echo "<br>User $this->testUser unban himself from the non-private room @imagine:";
        echo "<b>".$this->chat->sendCommand("/unban " . $this->testUser, "@imagine")."</b>";

        echo "<br>User $this->testUser is trying joining into @imagine after being kicked:";
        echo "<b>".$this->chat->sendCommand("/join @imagine", "@imagine")."</b>";
    }

    /** REBRE MISSATGES DE LA PILA D'USUARI */
    private function getMessagesTest() {

        echo "User $this->testUser gets all the messages from his messages pile: <br>";
        $xml = @simplexml_load_string($this->chat->getMessages($this->testUser));
        var_dump($xml);
    }

}

?>
