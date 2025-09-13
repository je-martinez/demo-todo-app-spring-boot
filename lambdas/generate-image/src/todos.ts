import { ObjectId } from "mongodb";
import { DatabaseHandler } from "./database";

type Todo = {
    _id:         ObjectId;
    title:       string;
    description: string;
    ownerId:     string;
    createdAt:   Date;
    completed:   boolean;
    cover:       CoverImage;
    _class:      string;
}

 type CoverImage = {
    uri: string | null;
    thumbnailUri: string | null;
    state: CoverImageState;
}

type CoverImageState = 'OK' | 'FAILED' | 'PROVISIONING'

const TODO_COLLECTION = "Todos";

export const getTodoById = async (todoId: string, databaseHandler: DatabaseHandler) => {
    const todo = await databaseHandler.getCollection<Todo>(TODO_COLLECTION).findOne({ _id: new ObjectId(todoId) });
    if (!todo) {
        throw new Error("Todo not found");
    }
    return todo;
}

export const appendCoverImageToTodo = async (todo: Todo, imageUrl: string, thumbnailUrl: string | null, databaseHandler: DatabaseHandler) => { 
    const coverImage: CoverImage = {
        uri: imageUrl,
        thumbnailUri: thumbnailUrl,
        state: 'OK'
    }
    await databaseHandler.getCollection<Todo>(TODO_COLLECTION).updateOne(
        { _id: todo._id }, 
        { $set: { cover: coverImage } },
        { upsert: true }
    );
    return coverImage;
}

export const markCoverImageAsFailed = async (todo: Todo, databaseHandler: DatabaseHandler) => {
    const coverImage: CoverImage = {
        uri: null,
        thumbnailUri: null,
        state: 'FAILED'
    }
    await databaseHandler.getCollection<Todo>(TODO_COLLECTION).updateOne({ _id: todo._id }, { $set: { cover: coverImage } });
    return coverImage;
}