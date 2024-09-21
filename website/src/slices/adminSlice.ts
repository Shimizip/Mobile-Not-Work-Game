import { createSlice, createAsyncThunk, PayloadAction } from "@reduxjs/toolkit";
import axiosInstance from "../api/axiosInstance";
import { AxiosError } from "axios";
import { showNotification, NotificationType } from "./notificationSlice";

type ErrorResponse = {
    message: string;
};

type AdminApiState = {
    status: "idle" | "loading" | "failed";
    error: string | null;
};

const initialState: AdminApiState = {
    status: "idle",
    error: null,
};

export const getSpielerAdmin = createAsyncThunk(
    "spieler",
    async ({ userId, token }: { userId: string; token: string }, { rejectWithValue }) => {
        try {
            console.log("getSpieler - userId:", userId);

            const response = await axiosInstance.get(`/spieler?sid=${userId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            console.log("Response:", response.data);
            return response.data;
        } catch (error) {
            if (error instanceof AxiosError && error.response) {
                const errorResponse = error.response.data;

                showNotification({
                    message: 'Fehler bei getSpieler!',
                    type: NotificationType.Error,
                });

                return rejectWithValue(errorResponse);
            }

            throw error;
        }
    }
);

export const freischaltenSpieler = createAsyncThunk(
    "admin/freischaltenSpieler",
    async ({ userId, token }: { userId: string; token: string }, { rejectWithValue }) => {
        try {
            console.log("freischaltenSpieler - userId:", userId);

            const headers = {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
            };

            const response = await axiosInstance.post(`/spieler/freischalten/${userId}`, {}, { headers });

            const resData = response.data;
            console.log("Response:", response.data);


            return resData;
        } catch (error) {
            if (error instanceof AxiosError && error.response) {
                const errorResponse = error.response.data;

                showNotification({
                    message: 'Fehler beim Freischalten des Spielers!',
                    type: NotificationType.Error,
                });

                return rejectWithValue(errorResponse);
            }

            throw error;
        }
    }
);

export const blockiereSpieler = createAsyncThunk(
    "admin/blockiereSpieler",
    async ({ userId, token }: { userId: string; token: string }, { rejectWithValue }) => {
        try {
            console.log("blockiereSpieler - userId:", userId);

            const headers = {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json',
            };

            const response = await axiosInstance.post(`/spieler/blockieren/${userId}`, {}, { headers });

            const resData = response.data;
            console.log("Response:", response.data);


            return resData;
        } catch (error) {
            if (error instanceof AxiosError && error.response) {
                const errorResponse = error.response.data;

                showNotification({
                    message: 'Fehler beim Blockieren des Spielers!',
                    type: NotificationType.Error,
                });

                return rejectWithValue(errorResponse);
            }

            throw error;
        }
    }
);

export const getScoreboard = createAsyncThunk(
    "scoreboard",
    async ({ userId, token }: { userId: string; token: string }, { rejectWithValue }) => {
        try {
            console.log("getScoreboard- spid:", userId);

            const response = await axiosInstance.get(`/spiel/scoreboard?spid=${userId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });
            console.log("Response:", response.data);
            return response.data;
        } catch (error) {
            if (error instanceof AxiosError && error.response) {
                const errorResponse = error.response.data;

                showNotification({
                    message: 'Fehler bei getScoreboard!',
                    type: NotificationType.Error,
                });

                return rejectWithValue(errorResponse);
            }

            throw error;
        }
    }
);

const adminSlice = createSlice({
    name: "admin",
    initialState,
    reducers: {},
    extraReducers: (builder) => {
        builder
            .addCase(freischaltenSpieler.pending, (state) => {
                state.status = "loading";
                state.error = null;
            })
            .addCase(freischaltenSpieler.fulfilled, (state) => {
                state.status = "idle";
            })
            .addCase(freischaltenSpieler.rejected, (state, action) => {
                state.status = "failed";
                if (action.payload) {
                    state.error =
                        (action.payload as ErrorResponse).message || "Freischaltung des Spielers fehlgeschlagen";
                } else {
                    state.error = action.error.message || "Freischaltung des Spielers fehlgeschlagen";
                }
            })
            .addCase(blockiereSpieler.pending, (state) => {
                state.status = "loading";
                state.error = null;
            })
            .addCase(blockiereSpieler.fulfilled, (state) => {
                state.status = "idle";
            })
            .addCase(blockiereSpieler.rejected, (state, action) => {
                state.status = "failed";
                if (action.payload) {
                    state.error =
                        (action.payload as ErrorResponse).message || "Blockieren des Spielers fehlgeschlagen";
                } else {
                    state.error = action.error.message || "Blockieren des Spielers fehlgeschlagen";
                }
            });
    },
});

export default adminSlice.reducer;
