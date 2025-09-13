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

/**
 * Get a todo by id
 * @param todoId - The id of the todo
 * @param databaseHandler - The database handler
 * @returns Promise<Todo> - The todo
 */

export const getTodoById = async (todoId: string, databaseHandler: DatabaseHandler) => {
    const todo = await databaseHandler.getCollection<Todo>(TODO_COLLECTION).findOne({ _id: new ObjectId(todoId) });
    if (!todo) {
        throw new Error("Todo not found");
    }
    return todo;
}


/**
 * Append a cover image to a todo
 * @param todo - The todo
 * @param imageUrl - The url of the image
 * @param thumbnailUrl - The url of the thumbnail
 * @param databaseHandler - The database handler
 * @returns Promise<CoverImage> - The cover image
 */
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


/**
 * Mark a cover image as failed
 * @param todo - The todo
 * @param databaseHandler - The database handler
 * @returns Promise<CoverImage> - The cover image
 */
export const markCoverImageAsFailed = async (todo: Todo, databaseHandler: DatabaseHandler) => {
    const coverImage: CoverImage = {
        uri: null,
        thumbnailUri: null,
        state: 'FAILED'
    }
    await databaseHandler.getCollection<Todo>(TODO_COLLECTION).updateOne({ _id: todo._id }, { $set: { cover: coverImage } });
    return coverImage;
}