import { LockOutlined } from "@mui/icons-material";
import {
  Container,
  CssBaseline,
  Box,
  Avatar,
  Typography,
  TextField,
  Button,
  Grid,
} from "@mui/material";
import { useState } from "react";
import { useAppDispatch } from "../hooks/redux-hooks";
import { login } from "../slices/authSlice";
import { showNotification, NotificationType } from "../slices/notificationSlice";

const Login = () => {
  const dispatch = useAppDispatch();

  const [username, setUsername] = useState("");
  const [passwort, setPasswort] = useState("");

  const handleLogin = async () => {
    if (username && passwort) {
      try {
        await dispatch(
          login({
            username,
            passwort,
          })
        ).unwrap();
      } catch (e) {
        console.error(e);
      }
    } else {
      // Show an error message.
      dispatch(
        showNotification({
            message: "Bitte alle Felder ausfüllen!",
            type: NotificationType.Error,
        })
      );
    }
  };

  return (
    <>
      <Container maxWidth="xs">
        <CssBaseline />
        <Box
          sx={{
            mt: 20,
            display: "flex",
            flexDirection: "column",
            alignItems: "center",
          }}
        >
          <Avatar sx={{ m: 1, bgcolor: "primary.light" }}>
            <LockOutlined />
          </Avatar>
          <Typography variant="h5">Mobile NotWork Game</Typography>
          <Typography variant="h5">Admin Login</Typography>
          <Box sx={{ mt: 1 }}>
            <TextField
              margin="normal"
              required
              fullWidth
              id="username"
              label="Benutzername"
              name="username"
              autoFocus
              value={username}
              onChange={(e) => setUsername(e.target.value)}
            />

            <TextField
              margin="normal"
              required
              fullWidth
              id="passwort"
              name="passwort"
              label="Passwort"
              type="passwort"
              value={passwort}
              onChange={(e) => {
                setPasswort(e.target.value);
              }}
            />

            <Button
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
              onClick={handleLogin}
            >
              Login
            </Button>
          </Box>
        </Box>
      </Container>
    </>
  );
};

export default Login;
