import datetime
from typing import Optional

from pydantic import BaseModel, Field

from database.models import SentStatus, Status


class StatusResponse(BaseModel):
    status: str


class ErrorResponse(BaseModel):
    reason: str


class AuthSignInPostResponse(BaseModel):
    token: str


class AuthSignInPostRequest(BaseModel):
    login: str = Field(max_length=50)
    password: str


class AuthRegisterPostRequest(BaseModel):
    login: str = Field(max_length=50)
    password: str
    name: str = Field(max_length=50)


class UserProfile(BaseModel):
    id: int
    name: str = Field(max_length=50)
    login: str = Field(max_length=50)
    is_admin: bool


class UserPublicProfile(BaseModel):
    id: int
    name: str = Field(max_length=50)
    login: str = Field(max_length=50)


class ProfileResponse(BaseModel):
    profile: UserProfile


class OrganizationCreatePostRequest(BaseModel):
    name: str = Field(max_length=50)
    description: str = Field(max_length=150)


class Organization(BaseModel):
    id: int
    name: str = Field(max_length=50)
    description: Optional[str] = Field(max_length=150)


class UserRight(BaseModel):
    name: str
    can_grant: bool


class OrganizationCreatePostResponse(BaseModel):
    organization: Organization


class OrganizationUser(BaseModel):
    user: UserPublicProfile
    rights: list[UserRight]


class UserOrganizationsGetResponse(BaseModel):
    organizations: list[Organization]


class Bot(BaseModel):
    bot_id: int
    bot_token: str


class OrganizationUsersGetResponse(BaseModel):
    users: list[OrganizationUser]


class AddBotPostRequest(BaseModel):
    token: str = Field(pattern=r'[0-9]+:[a-zA-Z\-_]+')


class AddBotPostResponse(BaseModel):
    id: int


class ListBotGetResponse(BaseModel):
    bots: list[Bot]


class AddUserPostRequest(BaseModel):
    login: str
    permissions: list[str]


class UserPostResponse(BaseModel):
    user: UserPublicProfile


class DeleteUserRequest(BaseModel):
    login: str


class DeleteUserResponse(BaseModel):
    user: UserPublicProfile


class AddChannelPostRequest(BaseModel):
    id: int
    bot_id: int


class AddChannelPostResponse(BaseModel):
    id: int
    bot_id: int
    name: str


class Channel(BaseModel):
    id: int
    bot_id: int
    name: str


class GetChannelsResponse(BaseModel):
    channels: list[Channel]


class DeleteChannelRequest(BaseModel):
    id: int


class DeleteChannelResponse(BaseModel):
    id: int


class PrivateSetPostStatusRequest(BaseModel):
    post_id: int
    post_status: str


class Post(BaseModel):
    id: Optional[int]
    organization_id: int
    created_by: int
    created_by_name: Optional[str] = None
    content: str
    revision_id: int
    is_approved: Status
    comment: Optional[str] = None
    planned_time: Optional[datetime.datetime] = None
    sent_status: SentStatus


class GetPostsResponse(BaseModel):
    posts: list[Post]


class AddNewPostRequest(BaseModel):
    content: str


class AddNewPostResponse(BaseModel):
    post: Post


class EditPostRequest(BaseModel):
    id: int
    content: Optional[str] = None
    is_approved: Optional[Status] = None
    comment: Optional[str] = None


class EditPostResponse(BaseModel):
    post: Post


class ScheduleTimeRequest(BaseModel):
    time: datetime.datetime


class PostIdResponse(BaseModel):
    id: int
